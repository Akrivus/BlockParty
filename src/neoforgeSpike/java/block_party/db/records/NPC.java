package block_party.db.records;

import block_party.db.BlockPartyDB;
import block_party.entities.Moe;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public record NPC(
        long databaseId,
        ResourceKey<Level> dimension,
        BlockPos pos,
        UUID playerUuid,
        boolean dead,
        String name,
        String gender,
        BlockState blockState,
        boolean hiding,
        BlockPos hiddenPos) {
    private static final UUID EMPTY_UUID = new UUID(0L, 0L);

    public static void createTable(BlockPartyDB db) throws SQLException {
        Connection connection = db.openConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS NPCs (
                        DatabaseID INTEGER PRIMARY KEY AUTOINCREMENT,
                        PosDim TEXT NOT NULL DEFAULT 'minecraft:overworld',
                        PosX INTEGER NOT NULL DEFAULT 0,
                        PosY INTEGER NOT NULL DEFAULT 0,
                        PosZ INTEGER NOT NULL DEFAULT 0,
                        PlayerUUID TEXT NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
                        Dead INTEGER NOT NULL DEFAULT 0,
                        Name TEXT NOT NULL DEFAULT '',
                        BlockState INTEGER NOT NULL DEFAULT 0,
                        Hiding INTEGER NOT NULL DEFAULT 0,
                        Gender TEXT NOT NULL DEFAULT 'female',
                        HiddenPosDim TEXT NOT NULL DEFAULT 'minecraft:overworld',
                        HiddenPosX INTEGER NOT NULL DEFAULT 0,
                        HiddenPosY INTEGER NOT NULL DEFAULT 0,
                        HiddenPosZ INTEGER NOT NULL DEFAULT 0
                    );
                    """);
        } finally {
            db.free(connection);
        }
    }

    public static NPC create(BlockPartyDB db, ServerLevel level, Moe moe) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO NPCs (
                    PosDim, PosX, PosY, PosZ, PlayerUUID, Dead, Name, BlockState, Hiding,
                    Gender, HiddenPosDim, HiddenPosX, HiddenPosY, HiddenPosZ
                ) VALUES (?, ?, ?, ?, ?, 0, ?, ?, 0, ?, ?, ?, ?, ?);
                """, Statement.RETURN_GENERATED_KEYS)) {
            bindPosition(statement, 1, level.dimension(), moe.blockPosition());
            statement.setString(5, moe.getOwnerUUID().toString());
            statement.setString(6, moe.getGivenName());
            statement.setInt(7, Block.getId(moe.getBlockState()));
            statement.setString(8, moe.getGender());
            bindPosition(statement, 9, level.dimension(), BlockPos.ZERO);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("NPC insert did not return a DatabaseID");
                }
                long id = keys.getLong(1);
                return find(db, id).orElseThrow(() -> new SQLException("Inserted NPC row " + id + " was not readable"));
            }
        } finally {
            db.free(connection);
        }
    }

    public static Optional<NPC> find(BlockPartyDB db, long id) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM NPCs WHERE DatabaseID = ? LIMIT 1;")) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(read(result)) : Optional.empty();
            }
        } finally {
            db.free(connection);
        }
    }

    public static void delete(BlockPartyDB db, long id) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM NPCs WHERE DatabaseID = ?;")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    public void updateFromMoe(BlockPartyDB db, ServerLevel level, Moe moe) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE NPCs
                SET PosDim = ?, PosX = ?, PosY = ?, PosZ = ?,
                    PlayerUUID = ?, Name = ?, BlockState = ?, Gender = ?
                WHERE DatabaseID = ?;
                """)) {
            bindPosition(statement, 1, level.dimension(), moe.blockPosition());
            statement.setString(5, moe.getOwnerUUID().toString());
            statement.setString(6, moe.getGivenName());
            statement.setInt(7, Block.getId(moe.getBlockState()));
            statement.setString(8, moe.getGender());
            statement.setLong(9, this.databaseId);
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    public void markHiding(BlockPartyDB db, ServerLevel level, BlockPos hiddenPos, BlockState hiddenState) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE NPCs
                SET Hiding = 1, BlockState = ?,
                    HiddenPosDim = ?, HiddenPosX = ?, HiddenPosY = ?, HiddenPosZ = ?
                WHERE DatabaseID = ?;
                """)) {
            statement.setInt(1, Block.getId(hiddenState));
            bindPosition(statement, 2, level.dimension(), hiddenPos);
            statement.setLong(6, this.databaseId);
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    public void markRevealed(BlockPartyDB db, ServerLevel level, BlockPos pos) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE NPCs
                SET Hiding = 0, PosDim = ?, PosX = ?, PosY = ?, PosZ = ?
                WHERE DatabaseID = ?;
                """)) {
            bindPosition(statement, 1, level.dimension(), pos);
            statement.setLong(5, this.databaseId);
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    public void markDead(BlockPartyDB db) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("UPDATE NPCs SET Dead = 1 WHERE DatabaseID = ?;")) {
            statement.setLong(1, this.databaseId);
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    public void applyTo(Moe moe) {
        moe.setDatabaseID(this.databaseId);
        moe.setOwnerUUID(this.playerUuid);
        moe.setBlockState(this.blockState);
        moe.setGivenName(this.name);
        moe.setGender(this.gender);
    }

    private static NPC read(ResultSet result) throws SQLException {
        return new NPC(
                result.getLong("DatabaseID"),
                Level.OVERWORLD,
                new BlockPos(result.getInt("PosX"), result.getInt("PosY"), result.getInt("PosZ")),
                parseUuid(result.getString("PlayerUUID")),
                result.getBoolean("Dead"),
                result.getString("Name"),
                result.getString("Gender"),
                Block.stateById(result.getInt("BlockState")),
                result.getBoolean("Hiding"),
                new BlockPos(result.getInt("HiddenPosX"), result.getInt("HiddenPosY"), result.getInt("HiddenPosZ")));
    }

    private static UUID parseUuid(String value) {
        return value == null || value.isBlank() ? EMPTY_UUID : UUID.fromString(value);
    }

    private static void bindPosition(PreparedStatement statement, int start, ResourceKey<Level> dimension, BlockPos pos) throws SQLException {
        statement.setString(start, dimension.location().toString());
        statement.setInt(start + 1, pos.getX());
        statement.setInt(start + 2, pos.getY());
        statement.setInt(start + 3, pos.getZ());
    }
}
