package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.items.CustomSpawnEggItem;
import block_party.network.CustomMessenger;
import block_party.network.payload.ControllerOpenPayload;
import block_party.network.payload.DialogueClosePayload;
import block_party.network.payload.DialogueOpenPayload;
import block_party.network.payload.DialogueRespondPayload;
import block_party.network.payload.NpcCallPayload;
import block_party.network.payload.NpcCallRequestPayload;
import block_party.network.payload.NpcDetailPayload;
import block_party.network.payload.NpcDetailRequestPayload;
import block_party.network.payload.NpcListPayload;
import block_party.network.payload.NpcListRequestPayload;
import block_party.network.payload.NpcRemoveRequestPayload;
import block_party.network.payload.ShrineListPayload;
import block_party.network.payload.ShrineListRequestPayload;
import block_party.registry.CustomBlocks;
import block_party.scene.Dialogue;
import block_party.scene.Response;
import block_party.scene.Speaker;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
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
        if (roundTrip(ShrineListRequestPayload.STREAM_CODEC, ShrineListRequestPayload.INSTANCE) != ShrineListRequestPayload.INSTANCE) {
            helper.fail("Expected shrine list request payload codec to preserve singleton instance");
            return;
        }
        assertEquals(helper, new NpcDetailRequestPayload(99L),
                roundTrip(NpcDetailRequestPayload.STREAM_CODEC, new NpcDetailRequestPayload(99L)),
                "detail request payload");
        assertEquals(helper, new NpcRemoveRequestPayload(100L),
                roundTrip(NpcRemoveRequestPayload.STREAM_CODEC, new NpcRemoveRequestPayload(100L)),
                "remove request payload");
        assertEquals(helper, new NpcCallRequestPayload(102L),
                roundTrip(NpcCallRequestPayload.STREAM_CODEC, new NpcCallRequestPayload(102L)),
                "call request payload");
        assertEquals(helper, new NpcListPayload(List.of(1L, 2L, 3L)),
                roundTrip(NpcListPayload.STREAM_CODEC, new NpcListPayload(List.of(1L, 2L, 3L))),
                "list response payload");
        assertEquals(helper, new NpcDetailPayload(101L, true, owner, "Moe", "female", 5, true, hiddenPos),
                roundTrip(NpcDetailPayload.STREAM_CODEC, new NpcDetailPayload(101L, true, owner, "Moe", "female", 5, true, hiddenPos)),
                "detail response payload");
        assertEquals(helper, new NpcCallPayload(103L, true, true, hiddenPos),
                roundTrip(NpcCallPayload.STREAM_CODEC, new NpcCallPayload(103L, true, true, hiddenPos)),
                "call response payload");
        assertEquals(helper, ControllerOpenPayload.cellPhone(List.of(1L, 2L), InteractionHand.MAIN_HAND),
                roundTrip(ControllerOpenPayload.STREAM_CODEC, ControllerOpenPayload.cellPhone(List.of(1L, 2L), InteractionHand.MAIN_HAND)),
                "cell phone open payload");
        assertEquals(helper, ControllerOpenPayload.yearbook(List.of(3L, 4L), 4L, InteractionHand.OFF_HAND),
                roundTrip(ControllerOpenPayload.STREAM_CODEC, ControllerOpenPayload.yearbook(List.of(3L, 4L), 4L, InteractionHand.OFF_HAND)),
                "yearbook open payload");
        Dialogue dialogue = sampleDialogue();
        NpcDetailPayload detail = new NpcDetailPayload(104L, true, owner, "Moe", "FEMALE", 5, false, BlockPos.ZERO);
        assertEquals(helper, new DialogueOpenPayload(detail, dialogue),
                roundTrip(DialogueOpenPayload.STREAM_CODEC, new DialogueOpenPayload(detail, dialogue)),
                "dialogue open payload");
        assertEquals(helper, new DialogueRespondPayload(105L, Response.LOVELY_HEART),
                roundTrip(DialogueRespondPayload.STREAM_CODEC, new DialogueRespondPayload(105L, Response.LOVELY_HEART)),
                "dialogue response payload");
        assertEquals(helper, new DialogueClosePayload(106L),
                roundTrip(DialogueClosePayload.STREAM_CODEC, new DialogueClosePayload(106L)),
                "dialogue close payload");
        assertEquals(helper, new ShrineListPayload(List.of(new BlockPos(7, 8, 9), new BlockPos(-1, 64, 2))),
                roundTrip(ShrineListPayload.STREAM_CODEC, new ShrineListPayload(List.of(new BlockPos(7, 8, 9), new BlockPos(-1, 64, 2)))),
                "shrine list payload");
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

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void callRequestMovesOwnedMoeAndReportsSuccess(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1209L, 2209L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }
        moe.setFollowing(false);
        BlockPos caller = helper.absolutePos(new BlockPos(4, 1, 1));

        NpcCallPayload response = CustomMessenger.callResponse(level, db, owner, caller, moe.getDatabaseID());
        if (!response.success()) {
            helper.fail("Expected owned call response to report success");
            return;
        }
        if (!response.following()) {
            helper.fail("Expected owned call response to report following=true");
            return;
        }
        if (!caller.east().equals(response.pos()) || !caller.east().equals(moe.blockPosition())) {
            helper.fail("Expected owned call to move Moe east of caller");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void callRequestFailurePayloadIsStable(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1210L, 2210L);
        UUID other = new UUID(1211L, 2211L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        NpcCallPayload missing = CustomMessenger.callResponse(level, db, owner, helper.absolutePos(new BlockPos(4, 1, 1)), Long.MAX_VALUE - 1210L);
        assertEquals(helper, new NpcCallPayload(Long.MAX_VALUE - 1210L, false, false, BlockPos.ZERO), missing, "missing call response");

        NpcCallPayload nonOwner = CustomMessenger.callResponse(level, db, other, helper.absolutePos(new BlockPos(4, 1, 1)), moe.getDatabaseID());
        assertEquals(helper, new NpcCallPayload(moe.getDatabaseID(), false, false, BlockPos.ZERO), nonOwner, "non-owner call response");
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void controllerOpenPayloadsUseOwnedNpcLists(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1212L, 2212L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        ControllerOpenPayload cellPhone = CustomMessenger.cellPhoneOpenPayload(db, owner, InteractionHand.MAIN_HAND);
        if (cellPhone.controller() != ControllerOpenPayload.ControllerType.CELL_PHONE || !cellPhone.databaseIds().contains(moe.getDatabaseID())) {
            helper.fail("Expected Cell Phone open payload to include owned NPC list");
            return;
        }
        ControllerOpenPayload yearbook = CustomMessenger.yearbookOpenPayload(db, owner, moe.getDatabaseID(), InteractionHand.OFF_HAND);
        if (yearbook.controller() != ControllerOpenPayload.ControllerType.YEARBOOK
                || yearbook.selectedDatabaseId() != moe.getDatabaseID()
                || !yearbook.databaseIds().contains(moe.getDatabaseID())) {
            helper.fail("Expected Yearbook open payload to include owned NPC list and selected ID");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dialogueResponseSetsOwnedLiveMoeState(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1213L, 2213L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }
        moe.setDialogue(sampleDialogue());

        if (!CustomMessenger.respondToDialogue(level, db, owner, moe.getDatabaseID(), Response.GREEN_CHECKMARK)) {
            helper.fail("Expected owned dialogue response to be accepted");
            return;
        }
        if (moe.getResponse() != Response.GREEN_CHECKMARK) {
            helper.fail("Expected owned dialogue response to update live Moe state");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dialogueCloseClearsOwnedLiveMoeState(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1214L, 2214L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }
        moe.setDialogue(sampleDialogue());
        moe.setResponse(Response.CHAT_BUBBLE);

        if (!CustomMessenger.closeDialogue(level, db, owner, moe.getDatabaseID())) {
            helper.fail("Expected owned dialogue close to be accepted");
            return;
        }
        if (moe.hasDialogue() || moe.hasResponse()) {
            helper.fail("Expected dialogue close to clear live Moe dialogue state");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void dialogueServerHandlersRejectMissingAndNonOwnedRows(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1215L, 2215L);
        UUID other = new UUID(1216L, 2216L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }
        moe.setDialogue(sampleDialogue());

        if (CustomMessenger.respondToDialogue(level, db, other, moe.getDatabaseID(), Response.RED_X)) {
            helper.fail("Expected non-owner dialogue response to be rejected");
            return;
        }
        if (moe.getResponse() == Response.RED_X) {
            helper.fail("Expected rejected response not to update live Moe state");
            return;
        }
        if (CustomMessenger.closeDialogue(level, db, owner, Long.MAX_VALUE - 1215L)) {
            helper.fail("Expected missing dialogue close to be rejected");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void shrineListRequestReturnsEmptyListWhenNoRowsMatch(GameTestHelper helper) {
        BlockPartyDB db = configuredDb(helper);
        UUID owner = new UUID(1217L, 2217L);
        deleteAllShrines(helper, db);

        ShrineListPayload response = CustomMessenger.shrineListResponse(db, owner, Level.NETHER);
        if (!response.positions().isEmpty()) {
            helper.fail("Expected empty shrine list response when no shrine rows match");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void shrineListRequestUsesForgeOwnerOrDimensionRules(GameTestHelper helper) {
        BlockPartyDB db = configuredDb(helper);
        UUID owner = new UUID(1218L, 2218L);
        UUID other = new UUID(1219L, 2219L);
        deleteAllShrines(helper, db);

        BlockPos ownedNether = new BlockPos(1, 70, 1);
        BlockPos sameDimensionOtherOwner = new BlockPos(2, 70, 2);
        BlockPos unrelated = new BlockPos(3, 70, 3);
        if (!insertShrine(helper, db, 121801L, owner, Level.NETHER, ownedNether)
                || !insertShrine(helper, db, 121802L, other, Level.OVERWORLD, sameDimensionOtherOwner)
                || !insertShrine(helper, db, 121803L, other, Level.NETHER, unrelated)) {
            return;
        }

        ShrineListPayload response = CustomMessenger.shrineListResponse(db, owner, Level.OVERWORLD);
        if (!response.positions().contains(ownedNether)) {
            helper.fail("Expected shrine list response to include owner shrine from another dimension");
            return;
        }
        if (!response.positions().contains(sameDimensionOtherOwner)) {
            helper.fail("Expected shrine list response to include same-dimension shrine from another owner");
            return;
        }
        if (response.positions().contains(unrelated) || response.positions().size() != 2) {
            helper.fail("Expected shrine list response to exclude unrelated owner/dimension rows");
            return;
        }
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

    private static BlockPartyDB configuredDb(GameTestHelper helper) {
        BlockPartyDB db = BlockPartyDB.get(helper.getLevel());
        db.configureDatabase(helper.getLevel().getServer());
        try {
            BlockPartyDB.createDataBlockTables(db);
        } catch (SQLException exception) {
            helper.fail("Expected data block tables to be creatable: " + exception.getMessage());
        }
        return db;
    }

    private static void deleteAllShrines(GameTestHelper helper, BlockPartyDB db) {
        try {
            Connection connection = db.openConnection();
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Shrines;")) {
                statement.executeUpdate();
            } finally {
                db.free(connection);
            }
        } catch (SQLException exception) {
            helper.fail("Expected shrine table cleanup to succeed: " + exception.getMessage());
        }
    }

    private static boolean insertShrine(GameTestHelper helper, BlockPartyDB db, long id, UUID owner, net.minecraft.resources.ResourceKey<Level> dimension, BlockPos pos) {
        try {
            Connection connection = db.openConnection();
            try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO Shrines (DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID)
                    VALUES (?, ?, ?, ?, ?, ?)
                    ON CONFLICT(DatabaseID) DO UPDATE SET
                        PosDim = excluded.PosDim,
                        PosX = excluded.PosX,
                        PosY = excluded.PosY,
                        PosZ = excluded.PosZ,
                        PlayerUUID = excluded.PlayerUUID;
                    """)) {
                statement.setLong(1, id);
                statement.setString(2, dimension.location().toString());
                statement.setInt(3, pos.getX());
                statement.setInt(4, pos.getY());
                statement.setInt(5, pos.getZ());
                statement.setString(6, owner.toString());
                statement.executeUpdate();
                return true;
            } finally {
                db.free(connection);
            }
        } catch (SQLException exception) {
            helper.fail("Expected shrine row insert to succeed: " + exception.getMessage());
            return false;
        }
    }

    private static Dialogue sampleDialogue() {
        return new Dialogue(
                "Hello there",
                false,
                new Speaker(Speaker.Identity.CHARACTER, Speaker.Position.LEFT, "DEFAULT", "HAPPY", true,
                        ResourceLocation.fromNamespaceAndPath("block_party", "moe.say"), 1.0F),
                ResourceLocation.fromNamespaceAndPath("block_party", "moe.say"),
                Map.of(Response.GREEN_CHECKMARK, "Yes", Response.RED_X, "No"));
    }

    private static <T> void assertEquals(GameTestHelper helper, T expected, T actual, String label) {
        if (!expected.equals(actual)) {
            helper.fail("Expected " + label + " to round-trip as " + expected + ", got " + actual);
        }
    }
}
