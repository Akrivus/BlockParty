package block_party.gametest;

import block_party.db.BlockPartyDB;
import block_party.entities.Moe;
import block_party.items.CustomSpawnEggItem;
import block_party.registry.CustomBlocks;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

final class MovementGameTestSupport {
    private MovementGameTestSupport() {
    }

    static Moe spawnMoe(GameTestHelper helper, ServerLevel level, UUID player, BlockPos relativeSource) {
        BlockPos source = helper.absolutePos(relativeSource);
        BlockState state = CustomBlocks.ENTRIES.get("sakura_log").get().defaultBlockState();
        level.setBlock(source, state, 3);
        Moe moe = CustomSpawnEggItem.spawnMoe(level, source, Direction.UP, player);
        if (moe == null) {
            helper.fail("Expected test Moe to spawn");
        }
        return moe;
    }

    static void insertLocation(BlockPartyDB db, UUID owner, ServerLevel level, BlockPos pos, String condition, int priority) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO %s (PosDim, PosX, PosY, PosZ, PlayerUUID, RequiredCondition, Priority)
                VALUES (?, ?, ?, ?, ?, ?, ?);
                """.formatted(BlockPartyDB.TABLE_LOCATIONS))) {
            bindPosition(statement, level, pos);
            statement.setString(5, owner.toString());
            statement.setString(6, condition);
            statement.setInt(7, priority);
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    static void insertSimpleDataBlock(BlockPartyDB db, String table, UUID owner, ServerLevel level, BlockPos pos) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO %s (PosDim, PosX, PosY, PosZ, PlayerUUID)
                VALUES (?, ?, ?, ?, ?);
                """.formatted(table))) {
            bindPosition(statement, level, pos);
            statement.setString(5, owner.toString());
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    static void buildLitDoorShelter(ServerLevel level, BlockPos houseSpot) {
        level.setBlock(houseSpot.above(2), Blocks.OAK_PLANKS.defaultBlockState(), 3);
        level.setBlock(houseSpot.north(), Blocks.OAK_PLANKS.defaultBlockState(), 3);
        level.setBlock(houseSpot.south(), Blocks.OAK_PLANKS.defaultBlockState(), 3);
        level.setBlock(houseSpot.west(), Blocks.OAK_PLANKS.defaultBlockState(), 3);
        level.setBlock(houseSpot.east(), Blocks.GLOWSTONE.defaultBlockState(), 3);
        level.setBlock(houseSpot.east().south(), Blocks.OAK_DOOR.defaultBlockState(), 3);
    }

    private static void bindPosition(PreparedStatement statement, ServerLevel level, BlockPos pos) throws SQLException {
        statement.setString(1, level.dimension().location().toString());
        statement.setInt(2, pos.getX());
        statement.setInt(3, pos.getY());
        statement.setInt(4, pos.getZ());
    }
}
