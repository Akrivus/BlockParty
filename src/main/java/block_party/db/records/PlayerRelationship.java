package block_party.db.records;

import block_party.db.BlockPartyDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record PlayerRelationship(
        long npcId,
        UUID playerUuid,
        boolean yearbookSigned,
        boolean phoneContact,
        float affection,
        float loyalty,
        float trust,
        float stress,
        long lastInteractionAt) {
    public static void createTable(BlockPartyDB db) throws SQLException {
        Connection connection = db.openConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS %s (
                        NpcID INTEGER NOT NULL,
                        PlayerUUID TEXT NOT NULL,
                        YearbookSigned INTEGER NOT NULL DEFAULT 0,
                        PhoneContact INTEGER NOT NULL DEFAULT 0,
                        Affection REAL NOT NULL DEFAULT 0.0,
                        Loyalty REAL NOT NULL DEFAULT 0.0,
                        Trust REAL NOT NULL DEFAULT 0.0,
                        Stress REAL NOT NULL DEFAULT 0.0,
                        LastInteractionAt INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY (NpcID, PlayerUUID)
                    );
                    """.formatted(BlockPartyDB.TABLE_PLAYER_RELATIONSHIPS));
        } finally {
            db.free(connection);
        }
    }

    public static PlayerRelationship ensure(BlockPartyDB db, long npcId, UUID playerUuid) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO %s (NpcID, PlayerUUID)
                VALUES (?, ?)
                ON CONFLICT(NpcID, PlayerUUID) DO NOTHING;
                """.formatted(BlockPartyDB.TABLE_PLAYER_RELATIONSHIPS))) {
            statement.setLong(1, npcId);
            statement.setString(2, playerUuid.toString());
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
        return find(db, npcId, playerUuid)
                .orElseThrow(() -> new SQLException("PlayerRelationship row was not readable after ensure"));
    }

    public static Optional<PlayerRelationship> find(BlockPartyDB db, long npcId, UUID playerUuid) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT * FROM %s
                WHERE NpcID = ? AND PlayerUUID = ?
                LIMIT 1;
                """.formatted(BlockPartyDB.TABLE_PLAYER_RELATIONSHIPS))) {
            statement.setLong(1, npcId);
            statement.setString(2, playerUuid.toString());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(read(result)) : Optional.empty();
            }
        } finally {
            db.free(connection);
        }
    }

    public static void setYearbookSigned(BlockPartyDB db, long npcId, UUID playerUuid, boolean signed) throws SQLException {
        ensure(db, npcId, playerUuid);
        updateBoolean(db, npcId, playerUuid, "YearbookSigned", signed);
    }

    public static void setPhoneContact(BlockPartyDB db, long npcId, UUID playerUuid, boolean contact) throws SQLException {
        ensure(db, npcId, playerUuid);
        updateBoolean(db, npcId, playerUuid, "PhoneContact", contact);
    }

    public static void setFeelings(BlockPartyDB db, long npcId, UUID playerUuid, float affection, float loyalty) throws SQLException {
        ensure(db, npcId, playerUuid);
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE %s
                SET Affection = ?, Loyalty = ?
                WHERE NpcID = ? AND PlayerUUID = ?;
                """.formatted(BlockPartyDB.TABLE_PLAYER_RELATIONSHIPS))) {
            statement.setFloat(1, affection);
            statement.setFloat(2, loyalty);
            statement.setLong(3, npcId);
            statement.setString(4, playerUuid.toString());
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    public static List<Long> listYearbookNpcIds(BlockPartyDB db, UUID playerUuid) throws SQLException {
        return listNpcIds(db, playerUuid, "YearbookSigned");
    }

    public static List<Long> listPhoneContactNpcIds(BlockPartyDB db, UUID playerUuid) throws SQLException {
        return listNpcIds(db, playerUuid, "PhoneContact");
    }

    private static void updateBoolean(BlockPartyDB db, long npcId, UUID playerUuid, String column, boolean value) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE %s
                SET %s = ?
                WHERE NpcID = ? AND PlayerUUID = ?;
                """.formatted(BlockPartyDB.TABLE_PLAYER_RELATIONSHIPS, column))) {
            statement.setBoolean(1, value);
            statement.setLong(2, npcId);
            statement.setString(3, playerUuid.toString());
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    private static List<Long> listNpcIds(BlockPartyDB db, UUID playerUuid, String column) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT NpcID FROM %s
                WHERE PlayerUUID = ? AND %s = 1
                ORDER BY NpcID ASC;
                """.formatted(BlockPartyDB.TABLE_PLAYER_RELATIONSHIPS, column))) {
            statement.setString(1, playerUuid.toString());
            List<Long> ids = new ArrayList<>();
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    ids.add(result.getLong("NpcID"));
                }
            }
            return List.copyOf(ids);
        } finally {
            db.free(connection);
        }
    }

    private static PlayerRelationship read(ResultSet result) throws SQLException {
        return new PlayerRelationship(
                result.getLong("NpcID"),
                UUID.fromString(result.getString("PlayerUUID")),
                result.getBoolean("YearbookSigned"),
                result.getBoolean("PhoneContact"),
                result.getFloat("Affection"),
                result.getFloat("Loyalty"),
                result.getFloat("Trust"),
                result.getFloat("Stress"),
                result.getLong("LastInteractionAt"));
    }
}
