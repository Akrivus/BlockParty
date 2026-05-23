package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.entities.goals.HideUntil;
import block_party.items.CustomSpawnEggItem;
import block_party.registry.CustomBlocks;
import block_party.world.chunk.ForcedChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class CellPhoneServiceGameTests {
    private CellPhoneServiceGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void ownerCanCallVisibleMoe(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1301L, 2301L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        BlockPos caller = helper.absolutePos(new BlockPos(4, 1, 1));
        Moe called = db.callOwnedNpc(level, owner, caller, moe.getDatabaseID()).orElse(null);
        if (called != moe) {
            helper.fail("Expected owner call to return the loaded Moe shell");
            return;
        }
        if (!called.blockPosition().equals(caller.east())) {
            helper.fail("Expected called Moe to move near caller");
            return;
        }
        helper.kill(called);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void callArrivalUsesForgeYawOffset(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1309L, 2309L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }
        Vec3 caller = Vec3.atBottomCenterOf(helper.absolutePos(new BlockPos(4, 1, 1)));

        Moe called = db.callOwnedNpc(level, owner, caller, 0.0F, moe.getDatabaseID()).orElse(null);
        if (called == null) {
            helper.fail("Expected owner call with yaw to succeed");
            return;
        }
        Vec3 expected = new Vec3(caller.x, caller.y, caller.z + 1.44D);
        if (called.position().distanceToSqr(expected) > 0.0001D) {
            helper.fail("Expected called Moe at " + expected + ", got " + called.position());
            return;
        }
        helper.kill(called);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void crossDimensionCallTeleportsLoadedMoe(GameTestHelper helper) {
        ServerLevel callerLevel = helper.getLevel();
        ServerLevel nether = callerLevel.getServer().getLevel(Level.NETHER);
        if (nether == null) {
            helper.fail("Expected Nether level to be available for cross-dimension phone call test");
            return;
        }

        BlockPartyDB db = BlockPartyDB.get(callerLevel);
        UUID owner = new UUID(1310L, 2310L);
        BlockPos source = new BlockPos(0, 80, 0);
        nether.getChunk(source);
        nether.setBlock(source, CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState(), 3);
        Moe moe = CustomSpawnEggItem.spawnMoe(nether, source, Direction.UP, owner);
        if (moe == null) {
            helper.fail("Expected Nether Moe spawn setup to succeed");
            return;
        }

        BlockPos caller = helper.absolutePos(new BlockPos(4, 1, 1));
        Moe called = db.callOwnedNpc(callerLevel, owner, caller, moe.getDatabaseID()).orElse(null);
        if (called == null) {
            helper.fail("Expected cross-dimension call to teleport loaded Moe");
            return;
        }

        if (called.level() != callerLevel) {
            helper.fail("Expected cross-dimension phone call to arrive in caller level");
            return;
        }
        if (!called.blockPosition().equals(caller.east())) {
            helper.fail("Expected cross-dimension called Moe to move near caller");
            return;
        }
        if (!called.isFollowing()) {
            helper.fail("Expected cross-dimension successful call to set following=true");
            return;
        }
        helper.kill(called);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void nonOwnerCannotCallMoe(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1302L, 2302L);
        UUID other = new UUID(1303L, 2303L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        BlockPos original = moe.blockPosition();
        if (db.callOwnedNpc(level, other, helper.absolutePos(new BlockPos(4, 1, 1)), moe.getDatabaseID()).isPresent()) {
            helper.fail("Expected non-owner call to fail safely");
            return;
        }
        if (!moe.blockPosition().equals(original)) {
            helper.fail("Expected rejected call not to move Moe");
            return;
        }
        helper.kill(moe);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void missingDeadCorruptAndUnloadedRowsFailSafely(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1304L, 2304L);
        BlockPos caller = helper.absolutePos(new BlockPos(4, 1, 1));

        if (db.callOwnedNpc(level, owner, caller, Long.MAX_VALUE - 1304L).isPresent()) {
            helper.fail("Expected missing row call to fail safely");
            return;
        }

        Moe dead = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (dead == null) {
            return;
        }
        long deadId = dead.getDatabaseID();
        NPC row = db.findNpcSafe(deadId).orElse(null);
        if (row == null) {
            helper.fail("Expected spawned row before dead-row call test");
            return;
        }
        try {
            row.markDead(db);
        } catch (SQLException exception) {
            helper.fail("Expected dead row setup to succeed: " + exception.getMessage());
            return;
        }
        if (db.callOwnedNpc(level, owner, caller, deadId).isPresent()) {
            helper.fail("Expected dead row call to fail safely");
            return;
        }
        helper.kill(dead);

        long corruptId = insertCorruptNpc(helper, db);
        if (corruptId < 0L) {
            return;
        }
        db.addTo(owner, corruptId);
        if (db.callOwnedNpc(level, owner, caller, corruptId).isPresent()) {
            helper.fail("Expected corrupt row call to fail safely");
            return;
        }

        Moe unloaded = spawnOwnedMoe(helper, level, owner, new BlockPos(3, 1, 1));
        if (unloaded == null) {
            return;
        }
        long unloadedId = unloaded.getDatabaseID();
        unloaded.discard();
        if (db.callOwnedNpc(level, owner, caller, unloadedId).isPresent()) {
            helper.fail("Expected row without a loaded live Moe to fail safely");
            return;
        }
        if (ForcedChunk.get(unloadedId) != null) {
            helper.fail("Expected failed unloaded call to release forced chunk");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void hiddenMoeCallFailsSafely(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1305L, 2305L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }

        MoeInHiding hidden = moe.hide(HideUntil.EXPOSED);
        if (hidden == null) {
            helper.fail("Expected hide setup to succeed");
            return;
        }
        if (db.callOwnedNpc(level, owner, helper.absolutePos(new BlockPos(4, 1, 1)), hidden.getDatabaseID()).isPresent()) {
            helper.fail("Expected hidden Moe call to fail safely until reveal behavior owns the transition");
            return;
        }
        helper.kill(hidden);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void followingFlagIsSetAfterSuccessfulCall(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1306L, 2306L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }
        moe.setFollowing(false);

        Moe called = db.callOwnedNpc(level, owner, helper.absolutePos(new BlockPos(4, 1, 1)), moe.getDatabaseID()).orElse(null);
        if (called == null) {
            helper.fail("Expected owner call to succeed");
            return;
        }
        if (!called.isFollowing()) {
            helper.fail("Expected successful call to set following=true");
            return;
        }
        helper.kill(called);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void farLoadedMoeCallQueuesAndReleasesForcedChunk(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1307L, 2307L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(12, 1, 1));
        if (moe == null) {
            return;
        }
        long id = moe.getDatabaseID();
        BlockPos caller = helper.absolutePos(new BlockPos(4, 1, 1));

        Moe called = db.callOwnedNpc(level, owner, caller, id).orElse(null);
        if (called != moe) {
            helper.fail("Expected far loaded Moe call to return the same live shell");
            return;
        }
        if (!called.blockPosition().equals(caller.east())) {
            helper.fail("Expected far loaded Moe to move near caller");
            return;
        }
        if (ForcedChunk.get(id) != null) {
            helper.fail("Expected successful far call to release forced chunk");
            return;
        }
        helper.kill(called);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void successfulCallReleasesPreexistingForcedChunk(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1308L, 2308L);
        Moe moe = spawnOwnedMoe(helper, level, owner, new BlockPos(1, 1, 1));
        if (moe == null) {
            return;
        }
        long id = moe.getDatabaseID();
        ForcedChunk.queue(id, level, new ChunkPos(moe.blockPosition()));
        if (ForcedChunk.get(id) == null) {
            helper.fail("Expected forced chunk setup to be tracked");
            return;
        }

        Moe called = db.callOwnedNpc(level, owner, helper.absolutePos(new BlockPos(4, 1, 1)), id).orElse(null);
        if (called == null) {
            helper.fail("Expected pre-forced call to succeed");
            return;
        }
        if (ForcedChunk.get(id) != null) {
            helper.fail("Expected successful call to release preexisting forced chunk");
            return;
        }
        helper.kill(called);
        helper.succeed();
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
}
