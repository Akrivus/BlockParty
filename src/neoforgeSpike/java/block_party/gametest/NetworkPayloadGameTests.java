package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.items.CustomSpawnEggItem;
import block_party.network.CustomMessenger;
import block_party.network.payload.NpcDetailPayload;
import block_party.network.payload.NpcDetailRequestPayload;
import block_party.network.payload.NpcListPayload;
import block_party.network.payload.NpcListRequestPayload;
import block_party.network.payload.NpcRemoveRequestPayload;
import block_party.registry.CustomBlocks;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class NetworkPayloadGameTests {
    private NetworkPayloadGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void npcPayloadCodecsRoundTrip(GameTestHelper helper) {
        UUID owner = new UUID(1201L, 2201L);
        BlockPos hiddenPos = new BlockPos(2, 3, 4);

        if (roundTrip(NpcListRequestPayload.STREAM_CODEC, NpcListRequestPayload.INSTANCE) != NpcListRequestPayload.INSTANCE) {
            helper.fail("Expected list request payload codec to preserve singleton instance");
            return;
        }
        assertEquals(helper, new NpcDetailRequestPayload(99L),
                roundTrip(NpcDetailRequestPayload.STREAM_CODEC, new NpcDetailRequestPayload(99L)),
                "detail request payload");
        assertEquals(helper, new NpcRemoveRequestPayload(100L),
                roundTrip(NpcRemoveRequestPayload.STREAM_CODEC, new NpcRemoveRequestPayload(100L)),
                "remove request payload");
        assertEquals(helper, new NpcListPayload(List.of(1L, 2L, 3L)),
                roundTrip(NpcListPayload.STREAM_CODEC, new NpcListPayload(List.of(1L, 2L, 3L))),
                "list response payload");
        assertEquals(helper, new NpcDetailPayload(101L, true, owner, "Moe", "female", 5, true, hiddenPos),
                roundTrip(NpcDetailPayload.STREAM_CODEC, new NpcDetailPayload(101L, true, owner, "Moe", "female", 5, true, hiddenPos)),
                "detail response payload");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void ownedListRequestReturnsOwnedNpcIds(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1202L, 2202L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        NpcListPayload response = CustomMessenger.listResponse(db, owner);
        if (!response.databaseIds().contains(moe.getDatabaseID())) {
            helper.fail("Expected owned list response to include spawned NPC ID");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void ownedDetailRequestReturnsRow(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1203L, 2203L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        NpcDetailPayload response = CustomMessenger.detailResponse(db, owner, moe.getDatabaseID());
        if (!response.found() || response.databaseId() != moe.getDatabaseID() || !owner.equals(response.ownerUuid())) {
            helper.fail("Expected owned detail response to expose row-backed identity");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void nonOwnedDetailRequestIsRejected(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1204L, 2204L);
        UUID other = new UUID(1205L, 2205L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        NpcDetailPayload response = CustomMessenger.detailResponse(db, other, moe.getDatabaseID());
        if (response.found()) {
            helper.fail("Expected non-owner detail response to fail safely");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void missingDeadAndCorruptRowsReturnSafeFailure(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1206L, 2206L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        if (CustomMessenger.detailResponse(db, owner, Long.MAX_VALUE - 789L).found()) {
            helper.fail("Expected missing row detail response to fail safely");
            return;
        }

        long id = moe.getDatabaseID();
        NPC row = db.findNpcSafe(id).orElse(null);
        if (row == null) {
            helper.fail("Expected spawned row before dead-row response test");
            return;
        }
        try {
            row.markDead(db);
        } catch (SQLException exception) {
            helper.fail("Expected dead row setup to succeed: " + exception.getMessage());
            return;
        }
        if (CustomMessenger.detailResponse(db, owner, id).found()) {
            helper.fail("Expected dead row detail response to fail safely");
            return;
        }

        long corruptId = insertCorruptNpc(helper, db);
        if (corruptId < 0L) {
            return;
        }
        db.addTo(owner, corruptId);
        if (CustomMessenger.detailResponse(db, owner, corruptId).found()) {
            helper.fail("Expected corrupt row detail response to fail safely");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void removeRequestOnlyAffectsOwnerList(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1207L, 2207L);
        UUID other = new UUID(1208L, 2208L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        long id = moe.getDatabaseID();
        NpcListPayload rejected = CustomMessenger.removeResponse(db, other, id);
        if (rejected.databaseIds().contains(id)) {
            helper.fail("Expected non-owner remove response to omit non-owned NPC ID");
            return;
        }
        if (!db.listNpcIds(owner).contains(id)) {
            helper.fail("Expected rejected remove to leave owner list intact");
            return;
        }

        NpcListPayload ownerResponse = CustomMessenger.removeResponse(db, owner, id);
        if (ownerResponse.databaseIds().contains(id)) {
            helper.fail("Expected owner remove response to omit removed NPC ID");
            return;
        }
        if (db.findNpcSafe(id).isEmpty()) {
            helper.fail("Expected remove request to de-list only, not delete SQLite row");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    private static <T> T roundTrip(StreamCodec<RegistryFriendlyByteBuf, T> codec, T payload) {
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
        codec.encode(buffer, payload);
        return codec.decode(buffer);
    }

    private static Moe spawnOwnedMoe(GameTestHelper helper, ServerLevel level, UUID owner, BlockPos relativeSource) {
        BlockPos source = helper.absolutePos(relativeSource);
        BlockState state = CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState();
        level.setBlock(source, state, 3);
        Moe moe = CustomSpawnEggItem.spawnMoe(level, source, Direction.UP, owner);
        if (moe == null) {
            helper.fail("Expected owned Moe spawn to succeed");
        }
        return moe;
    }

    private static long insertCorruptNpc(GameTestHelper helper, BlockPartyDB db) {
        try {
            Connection connection = db.openConnection();
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO NPCs (PlayerUUID, Name, BlockState, Gender)
                    VALUES ('not-a-uuid', 'Corrupt', 0, 'female');
                    """, Statement.RETURN_GENERATED_KEYS)) {
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        helper.fail("Expected corrupt row insert to return an ID");
                        return -1L;
                    }
                    return keys.getLong(1);
                }
            } finally {
                db.free(connection);
            }
        } catch (SQLException exception) {
            helper.fail("Expected corrupt row setup to succeed: " + exception.getMessage());
            return -1L;
        }
    }

    private static <T> void assertEquals(GameTestHelper helper, T expected, T actual, String label) {
        if (!expected.equals(actual)) {
            helper.fail("Expected " + label + " to round-trip as " + expected + ", got " + actual);
        }
    }
}
