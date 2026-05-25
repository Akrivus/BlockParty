package block_party.gametest;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.TsukumogamiCandidate;
import block_party.entities.Moe;
import block_party.entities.movement.RoutineIntent;
import block_party.registry.CustomEntities;
import block_party.world.TsukumogamiSpawns;
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
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(BlockParty.ID)
@PrefixGameTestTemplate(false)
public final class TsukumogamiGameTests {
    private TsukumogamiGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void placedCandidateBlockNearShrineCreatesProspectiveSpawn(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPartyDB db = BlockPartyDB.get(level);
        UUID owner = new UUID(1801L, 2801L);
        BlockPos shrine = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos pos = helper.absolutePos(new BlockPos(5, 1, 1));
        try {
            clearTsukumogamiState(db);
            insertShrine(db, level, shrine, owner);
        } catch (SQLException exception) {
            helper.fail("Expected shrine setup to succeed: " + exception.getMessage());
            return;
        }
        BlockState state = Blocks.CRAFTING_TABLE.defaultBlockState();
        level.setBlock(pos, state, 3);

        if (!TsukumogamiSpawns.trackPlacedBlock(level, pos, state, owner)) {
            helper.fail("Expected tsukumogami candidate tracking to accept crafting table near shrine");
            return;
        }
        try {
            TsukumogamiCandidate candidate = db.findTsukumogamiCandidate(level, pos).orElse(null);
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
            clearTsukumogamiState(db);
        } catch (SQLException exception) {
            helper.fail("Expected tsukumogami state cleanup to succeed: " + exception.getMessage());
            return;
        }
        level.setBlock(pos, state, 3);

        if (TsukumogamiSpawns.trackPlacedBlock(level, pos, state, owner)) {
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
            clearTsukumogamiState(db);
            insertShrine(db, level, shrine, owner);
        } catch (SQLException exception) {
            helper.fail("Expected shrine setup to succeed: " + exception.getMessage());
            return;
        }
        BlockState state = Blocks.CRAFTING_TABLE.defaultBlockState();
        level.setBlock(pos, state, 3);
        TsukumogamiSpawns.trackPlacedBlock(level, pos, state, owner);

        int spawned = TsukumogamiSpawns.matureCandidates(level, level.getGameTime() + TsukumogamiSpawns.MATURATION_TICKS);
        if (spawned != 1) {
            helper.fail("Expected one mature tsukumogami candidate to spawn, got " + spawned);
            return;
        }
        List<Moe> moes = level.getEntitiesOfClass(Moe.class, new net.minecraft.world.phys.AABB(pos.above()).inflate(1.0D));
        if (moes.size() != 1) {
            helper.fail("Expected spawned tsukumogami Moe above source block, got " + moes.size());
            return;
        }
        Moe moe = moes.getFirst();
        if (moe.isCardinal() || !moe.hasHome() || !moe.getHome().getPos().equals(pos) || moe.getRoutineIntent() != RoutineIntent.REST) {
            helper.fail("Expected spawned tsukumogami to be non-cardinal, resting, and homed to source block");
            return;
        }
        if (!level.getBlockState(pos).isAir()) {
            helper.fail("Expected source block to be removed while tsukumogami Moe is active");
            return;
        }
        helper.kill(moe);
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
            clearTsukumogamiState(db);
            insertShrine(db, level, shrine, owner);
        } catch (SQLException exception) {
            helper.fail("Expected shrine setup to succeed: " + exception.getMessage());
            return;
        }
        BlockState state = Blocks.CRAFTING_TABLE.defaultBlockState();
        level.setBlock(pos, state, 3);
        level.setBlock(pos.east(), Blocks.LEVER.defaultBlockState(), 3);
        TsukumogamiSpawns.trackPlacedBlock(level, pos, state, owner);

        int spawned = TsukumogamiSpawns.matureCandidates(level, level.getGameTime() + TsukumogamiSpawns.MATURATION_TICKS);
        if (spawned != 0) {
            helper.fail("Expected redstone-adjacent candidate to be discarded without spawning");
            return;
        }
        try {
            if (db.findTsukumogamiCandidate(level, pos).isPresent()) {
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
                INSERT INTO Shrines (DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID)
                VALUES (?, ?, ?, ?, ?, ?);
                """)) {
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

    private static void clearTsukumogamiState(BlockPartyDB db) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement shrineStatement = connection.prepareStatement("DELETE FROM Shrines;");
             PreparedStatement candidateStatement = connection.prepareStatement("DELETE FROM TsukumogamiCandidates;")) {
            shrineStatement.executeUpdate();
            candidateStatement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }
}
