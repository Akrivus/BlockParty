package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.AwakeningOpportunity;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import block_party.entities.movement.RoutineIntent;
import block_party.registry.CustomEntities;
import block_party.world.AwakeningService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class AwakeningGameTests {
    private AwakeningGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void placedCandidateBlockNearShrineCreatesProspectiveSpawn(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1801L, 2801L);
        BlockPos shrine = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos pos = helper.absolutePos(new BlockPos(5, 1, 1));
        try {
            clearAwakeningState(db);
            insertShrine(db, level, shrine, owner);
        } catch (SQLException exception) {
            helper.fail("Expected shrine setup to succeed: " + exception.getMessage());
            return;
        }
        BlockState state = Blocks.CRAFTING_TABLE.defaultBlockState();
        level.setBlock(pos, state, 3);

        if (!AwakeningService.trackPlacedBlock(level, pos, state, owner)) {
            helper.fail("Expected awakening opportunity tracking to accept crafting table near shrine");
            return;
        }
        try {
            AwakeningOpportunity candidate = db.findAwakeningOpportunity(level, pos).orElse(null);
            if (candidate == null || !owner.equals(candidate.playerUuid()) || candidate.matureAtGameTime() <= candidate.createdGameTime()) {
                helper.fail("Expected prospective spawn candidate row with owner and future maturity");
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected candidate lookup to succeed: " + exception.getMessage());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void candidateBlockOutsideShrineInfluenceIsIgnored(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1802L, 2802L);
        BlockPos pos = helper.absolutePos(new BlockPos(5, 1, 1));
        BlockState state = Blocks.CRAFTING_TABLE.defaultBlockState();
        try {
            clearAwakeningState(db);
        } catch (SQLException exception) {
            helper.fail("Expected awakening state cleanup to succeed: " + exception.getMessage());
            return;
        }
        level.setBlock(pos, state, 3);

        if (AwakeningService.trackPlacedBlock(level, pos, state, owner)) {
            helper.fail("Expected candidate tracking to ignore blocks outside shrine influence");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void matureValidCandidateSpawnsNonCardinalMoeAtSourceHome(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1803L, 2803L);
        BlockPos shrine = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos pos = helper.absolutePos(new BlockPos(5, 1, 1));
        try {
            clearAwakeningState(db);
            insertShrine(db, level, shrine, owner);
        } catch (SQLException exception) {
            helper.fail("Expected shrine setup to succeed: " + exception.getMessage());
            return;
        }
        BlockState state = Blocks.CRAFTING_TABLE.defaultBlockState();
        level.setBlock(pos, state, 3);
        AwakeningService.trackPlacedBlock(level, pos, state, owner);

        int spawned = AwakeningService.matureOpportunities(level, level.getGameTime() + AwakeningService.MATURATION_TICKS);
        if (spawned != 1) {
            helper.fail("Expected one mature awakening opportunity to spawn, got " + spawned);
            return;
        }
        List<Moe> moes = level.getEntitiesOfClass(Moe.class, new AABB(pos.above()).inflate(1.0D));
        if (moes.size() != 1) {
            helper.fail("Expected spawned awakened Moe above source block, got " + moes.size());
            return;
        }
        Moe moe = moes.getFirst();
        if (moe.isCardinal() || !moe.hasHome() || !moe.getHome().getPos().equals(pos) || moe.getRoutineIntent() != RoutineIntent.SLEEP) {
            helper.fail("Expected awakened Moe to be non-cardinal, sleeping, and homed to its source block");
            return;
        }
        if (!level.getBlockState(pos).isAir()) {
            helper.fail("Expected source block to be removed while awakened Moe is active");
            return;
        }
        moe.moveToBlock(pos);
        if (!moe.routine().updateMovement()) {
            helper.fail("Expected awakened Moe to hide after returning home");
            return;
        }
        List<MoeInHiding> hidden = level.getEntitiesOfClass(MoeInHiding.class, new AABB(pos).inflate(1.0D));
        if (hidden.size() != 1 || !level.getBlockState(pos).equals(state)) {
            helper.fail("Expected awakened Moe to restore its block and inhabit the source position");
            return;
        }
        helper.kill(hidden.getFirst());
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void redstoneAdjacentCandidateIsDiscardedWithoutSpawning(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1804L, 2804L);
        BlockPos shrine = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos pos = helper.absolutePos(new BlockPos(5, 1, 1));
        try {
            clearAwakeningState(db);
            insertShrine(db, level, shrine, owner);
        } catch (SQLException exception) {
            helper.fail("Expected shrine setup to succeed: " + exception.getMessage());
            return;
        }
        BlockState state = Blocks.CRAFTING_TABLE.defaultBlockState();
        level.setBlock(pos, state, 3);
        level.setBlock(pos.east(), Blocks.LEVER.defaultBlockState(), 3);
        AwakeningService.trackPlacedBlock(level, pos, state, owner);

        int spawned = AwakeningService.matureOpportunities(level, level.getGameTime() + AwakeningService.MATURATION_TICKS);
        if (spawned != 0) {
            helper.fail("Expected redstone-adjacent candidate to be discarded without spawning");
            return;
        }
        try {
            if (db.findAwakeningOpportunity(level, pos).isPresent()) {
                helper.fail("Expected invalid redstone candidate row to be deleted");
                return;
            }
        } catch (SQLException exception) {
            helper.fail("Expected candidate lookup to succeed: " + exception.getMessage());
            return;
        }
        if (!level.getBlockState(pos).equals(state)) {
            helper.fail("Expected invalid candidate block to remain in world");
            return;
        }
        helper.succeed();
    }

    private static void insertShrine(BlockPartyDB db, ServerLevel level, BlockPos pos, UUID owner) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO %s (DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID)
                VALUES (?, ?, ?, ?, ?, ?);
                """.formatted(BlockPartyDB.TABLE_SHRINES))) {
            statement.setLong(1, Math.abs(pos.asLong()));
            statement.setString(2, level.dimension().location().toString());
            statement.setInt(3, pos.getX());
            statement.setInt(4, pos.getY());
            statement.setInt(5, pos.getZ());
            statement.setString(6, owner.toString());
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    private static void clearAwakeningState(BlockPartyDB db) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement shrineStatement = connection.prepareStatement("DELETE FROM " + BlockPartyDB.TABLE_SHRINES + ";");
             PreparedStatement candidateStatement = connection.prepareStatement("DELETE FROM " + BlockPartyDB.TABLE_AWAKENING_OPPORTUNITIES + ";")) {
            shrineStatement.executeUpdate();
            candidateStatement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }
}
