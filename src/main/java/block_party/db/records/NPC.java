package block_party.db.records;

import block_party.db.BlockPartyDB;
import block_party.db.DimBlockPos;
import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.entities.Moe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public record NPC(
        long databaseId,
        ResourceKey<Level> dimension,
        BlockPos pos,
        UUID playerUuid,
        boolean dead,
        String name,
        String gender,
        String bloodType,
        String dere,
        String zodiac,
        String emotion,
        BlockState blockState,
        BlockState visibleBlockState,
        float scale,
        boolean corporeal,
        float health,
        float foodLevel,
        float exhaustion,
        float saturation,
        float stress,
        float relaxation,
        float loyalty,
        float affection,
        float slouch,
        float age,
        long lastSeenAt,
        boolean hasHome,
        DimBlockPos home,
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
                        VisibleBlockState INTEGER NOT NULL DEFAULT 0,
                        Hiding INTEGER NOT NULL DEFAULT 0,
                        Gender TEXT NOT NULL DEFAULT 'FEMALE',
                        BloodType TEXT NOT NULL DEFAULT 'O',
                        Dere TEXT NOT NULL DEFAULT 'NYANDERE',
                        Zodiac TEXT NOT NULL DEFAULT 'ARIES',
                        Emotion TEXT NOT NULL DEFAULT 'NORMAL',
                        Scale REAL NOT NULL DEFAULT 1.0,
                        IsCorporeal INTEGER NOT NULL DEFAULT 1,
                        Health REAL NOT NULL DEFAULT 20.0,
                        FoodLevel REAL NOT NULL DEFAULT 20.0,
                        Exhaustion REAL NOT NULL DEFAULT 0.0,
                        Saturation REAL NOT NULL DEFAULT 6.0,
                        Stress REAL NOT NULL DEFAULT 0.0,
                        Relaxation REAL NOT NULL DEFAULT 0.0,
                        Loyalty REAL NOT NULL DEFAULT 6.0,
                        Affection REAL NOT NULL DEFAULT 0.0,
                        Slouch REAL NOT NULL DEFAULT 0.0,
                        Age REAL NOT NULL DEFAULT 0.0,
                        LastSeenAt INTEGER NOT NULL DEFAULT 0,
                        HasHome INTEGER NOT NULL DEFAULT 0,
                        HomePosDim TEXT NOT NULL DEFAULT 'minecraft:overworld',
                        HomePosX INTEGER NOT NULL DEFAULT 0,
                        HomePosY INTEGER NOT NULL DEFAULT 0,
                        HomePosZ INTEGER NOT NULL DEFAULT 0,
                        HiddenPosDim TEXT NOT NULL DEFAULT 'minecraft:overworld',
                        HiddenPosX INTEGER NOT NULL DEFAULT 0,
                        HiddenPosY INTEGER NOT NULL DEFAULT 0,
                        HiddenPosZ INTEGER NOT NULL DEFAULT 0
                    );
                    """);
            ensureColumn(statement, "VisibleBlockState", "INTEGER NOT NULL DEFAULT 0");
            ensureColumn(statement, "Gender", "TEXT NOT NULL DEFAULT 'FEMALE'");
            ensureColumn(statement, "BloodType", "TEXT NOT NULL DEFAULT 'O'");
            ensureColumn(statement, "Dere", "TEXT NOT NULL DEFAULT 'NYANDERE'");
            ensureColumn(statement, "Zodiac", "TEXT NOT NULL DEFAULT 'ARIES'");
            ensureColumn(statement, "Emotion", "TEXT NOT NULL DEFAULT 'NORMAL'");
            ensureColumn(statement, "Scale", "REAL NOT NULL DEFAULT 1.0");
            ensureColumn(statement, "IsCorporeal", "INTEGER NOT NULL DEFAULT 1");
            ensureColumn(statement, "Health", "REAL NOT NULL DEFAULT 20.0");
            ensureColumn(statement, "FoodLevel", "REAL NOT NULL DEFAULT 20.0");
            ensureColumn(statement, "Exhaustion", "REAL NOT NULL DEFAULT 0.0");
            ensureColumn(statement, "Saturation", "REAL NOT NULL DEFAULT 6.0");
            ensureColumn(statement, "Stress", "REAL NOT NULL DEFAULT 0.0");
            ensureColumn(statement, "Relaxation", "REAL NOT NULL DEFAULT 0.0");
            ensureColumn(statement, "Loyalty", "REAL NOT NULL DEFAULT 6.0");
            ensureColumn(statement, "Affection", "REAL NOT NULL DEFAULT 0.0");
            ensureColumn(statement, "Slouch", "REAL NOT NULL DEFAULT 0.0");
            ensureColumn(statement, "Age", "REAL NOT NULL DEFAULT 0.0");
            ensureColumn(statement, "LastSeenAt", "INTEGER NOT NULL DEFAULT 0");
            ensureColumn(statement, "HasHome", "INTEGER NOT NULL DEFAULT 0");
            ensureColumn(statement, "HomePosDim", "TEXT NOT NULL DEFAULT 'minecraft:overworld'");
            ensureColumn(statement, "HomePosX", "INTEGER NOT NULL DEFAULT 0");
            ensureColumn(statement, "HomePosY", "INTEGER NOT NULL DEFAULT 0");
            ensureColumn(statement, "HomePosZ", "INTEGER NOT NULL DEFAULT 0");
            ensureColumn(statement, "HiddenPosDim", "TEXT NOT NULL DEFAULT 'minecraft:overworld'");
            ensureColumn(statement, "HiddenPosX", "INTEGER NOT NULL DEFAULT 0");
            ensureColumn(statement, "HiddenPosY", "INTEGER NOT NULL DEFAULT 0");
            ensureColumn(statement, "HiddenPosZ", "INTEGER NOT NULL DEFAULT 0");
        } finally {
            db.free(connection);
        }
    }

    private static void ensureColumn(Statement statement, String column, String definition) throws SQLException {
        Set<String> columns = new HashSet<>();
        try (ResultSet result = statement.executeQuery("PRAGMA table_info(NPCs);")) {
            while (result.next()) {
                columns.add(result.getString("name"));
            }
        }
        if (!columns.contains(column)) {
            statement.execute("ALTER TABLE NPCs ADD COLUMN " + column + " " + definition + ";");
        }
    }

    public static NPC create(BlockPartyDB db, ServerLevel level, Moe moe) throws SQLException {
        return create(db, level, moe, null, false, BlockPos.ZERO);
    }

    public static NPC create(BlockPartyDB db, ServerLevel level, Moe moe, Long databaseId, boolean hiding, BlockPos hiddenPos) throws SQLException {
        Connection connection = db.openConnection();
        boolean explicitId = databaseId != null;
        String sql = explicitId ? """
                INSERT INTO NPCs (
                    DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID, Dead, Name, BlockState, VisibleBlockState, Hiding,
                    Gender, BloodType, Dere, Zodiac, Emotion, Scale, IsCorporeal, Health,
                    FoodLevel, Exhaustion, Saturation, Stress, Relaxation, Loyalty, Affection, Slouch, Age,
                    LastSeenAt, HasHome, HomePosDim, HomePosX, HomePosY, HomePosZ,
                    HiddenPosDim, HiddenPosX, HiddenPosY, HiddenPosZ
                ) VALUES (?, ?, ?, ?, ?, ?, 0, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(DatabaseID) DO UPDATE SET
                    PosDim = excluded.PosDim,
                    PosX = excluded.PosX,
                    PosY = excluded.PosY,
                    PosZ = excluded.PosZ,
                    PlayerUUID = excluded.PlayerUUID,
                    Name = excluded.Name,
                    BlockState = excluded.BlockState,
                    VisibleBlockState = excluded.VisibleBlockState,
                    Hiding = excluded.Hiding,
                    Gender = excluded.Gender,
                    BloodType = excluded.BloodType,
                    Dere = excluded.Dere,
                    Zodiac = excluded.Zodiac,
                    Emotion = excluded.Emotion,
                    Scale = excluded.Scale,
                    IsCorporeal = excluded.IsCorporeal,
                    Health = excluded.Health,
                    FoodLevel = excluded.FoodLevel,
                    Exhaustion = excluded.Exhaustion,
                    Saturation = excluded.Saturation,
                    Stress = excluded.Stress,
                    Relaxation = excluded.Relaxation,
                    Loyalty = excluded.Loyalty,
                    Affection = excluded.Affection,
                    Slouch = excluded.Slouch,
                    Age = excluded.Age,
                    LastSeenAt = excluded.LastSeenAt,
                    HasHome = excluded.HasHome,
                    HomePosDim = excluded.HomePosDim,
                    HomePosX = excluded.HomePosX,
                    HomePosY = excluded.HomePosY,
                    HomePosZ = excluded.HomePosZ,
                    HiddenPosDim = excluded.HiddenPosDim,
                    HiddenPosX = excluded.HiddenPosX,
                    HiddenPosY = excluded.HiddenPosY,
                    HiddenPosZ = excluded.HiddenPosZ;
                """ : """
                INSERT INTO NPCs (
                    PosDim, PosX, PosY, PosZ, PlayerUUID, Dead, Name, BlockState, VisibleBlockState, Hiding,
                    Gender, BloodType, Dere, Zodiac, Emotion, Scale, IsCorporeal, Health,
                    FoodLevel, Exhaustion, Saturation, Stress, Relaxation, Loyalty, Affection, Slouch, Age,
                    LastSeenAt, HasHome, HomePosDim, HomePosX, HomePosY, HomePosZ,
                    HiddenPosDim, HiddenPosX, HiddenPosY, HiddenPosZ
                ) VALUES (?, ?, ?, ?, ?, 0, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql, explicitId ? Statement.NO_GENERATED_KEYS : Statement.RETURN_GENERATED_KEYS)) {
            int offset = explicitId ? 1 : 0;
            if (explicitId) {
                statement.setLong(1, databaseId);
            }
            bindPosition(statement, 1 + offset, level.dimension(), moe.blockPosition());
            statement.setString(5 + offset, moe.getOwnerUUID().toString());
            statement.setString(6 + offset, moe.getGivenName());
            statement.setInt(7 + offset, Block.getId(moe.getBlockState()));
            statement.setInt(8 + offset, Block.getId(moe.getVisibleBlockState()));
            statement.setBoolean(9 + offset, hiding);
            statement.setString(10 + offset, moe.getGender());
            statement.setString(11 + offset, moe.getBloodType());
            statement.setString(12 + offset, moe.getDere());
            statement.setString(13 + offset, moe.getZodiac());
            statement.setString(14 + offset, moe.getEmotion());
            statement.setFloat(15 + offset, moe.getMoeScale());
            statement.setBoolean(16 + offset, moe.isCorporeal());
            statement.setFloat(17 + offset, moe.getHealth());
            statement.setFloat(18 + offset, moe.getFoodLevel());
            statement.setFloat(19 + offset, moe.getExhaustion());
            statement.setFloat(20 + offset, moe.getSaturation());
            statement.setFloat(21 + offset, moe.getStress());
            statement.setFloat(22 + offset, moe.getRelaxation());
            statement.setFloat(23 + offset, moe.getLoyalty());
            statement.setFloat(24 + offset, moe.getAffection());
            statement.setFloat(25 + offset, moe.getSlouch());
            statement.setFloat(26 + offset, moe.getAge());
            statement.setLong(27 + offset, moe.getLastSeen());
            statement.setBoolean(28 + offset, moe.hasHome());
            bindPosition(statement, 29 + offset, moe.getHome().getDim(), moe.getHome().getPos());
            bindPosition(statement, 33 + offset, level.dimension(), hiddenPos);
            statement.executeUpdate();
            if (explicitId) {
                return find(db, databaseId).orElseThrow(() -> new SQLException("Upserted NPC row " + databaseId + " was not readable"));
            }
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

    public static NPC createFromShimenawa(BlockPartyDB db, ServerLevel level, ShimenawaBlockEntity entity) throws SQLException {
        Moe moe = new Moe(block_party.registry.CustomEntities.MOE.get(), level);
        moe.setDatabaseID(entity.getDatabaseID());
        moe.moveToBlock(entity.getBlockPos());
        moe.setOwnerUUID(entity.getPlayerUUID());
        moe.setBlockState(entity.getBlockState());
        CompoundTag data = entity.getPersistentData();
        applyPersistentString(data, "Name", moe::setGivenName);
        applyPersistentString(data, "GivenName", moe::setGivenName);
        applyPersistentString(data, "Gender", moe::setGender);
        applyPersistentString(data, "BloodType", moe::setBloodType);
        applyPersistentString(data, "Dere", moe::setDere);
        applyPersistentString(data, "Zodiac", moe::setZodiac);
        applyPersistentString(data, "Emotion", moe::setEmotion);
        return create(db, level, moe, entity.getDatabaseID(), true, entity.getBlockPos());
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
                    PlayerUUID = ?, Name = ?, BlockState = ?, VisibleBlockState = ?,
                    Gender = ?, BloodType = ?, Dere = ?, Zodiac = ?, Emotion = ?,
                    Scale = ?, IsCorporeal = ?, Health = ?,
                    FoodLevel = ?, Exhaustion = ?, Saturation = ?, Stress = ?, Relaxation = ?,
                    Loyalty = ?, Affection = ?, Slouch = ?, Age = ?,
                    LastSeenAt = ?, HasHome = ?,
                    HomePosDim = ?, HomePosX = ?, HomePosY = ?, HomePosZ = ?
                WHERE DatabaseID = ?;
                """)) {
            bindPosition(statement, 1, level.dimension(), moe.blockPosition());
            statement.setString(5, moe.getOwnerUUID().toString());
            statement.setString(6, moe.getGivenName());
            statement.setInt(7, Block.getId(moe.getBlockState()));
            statement.setInt(8, Block.getId(moe.getVisibleBlockState()));
            statement.setString(9, moe.getGender());
            statement.setString(10, moe.getBloodType());
            statement.setString(11, moe.getDere());
            statement.setString(12, moe.getZodiac());
            statement.setString(13, moe.getEmotion());
            statement.setFloat(14, moe.getMoeScale());
            statement.setBoolean(15, moe.isCorporeal());
            statement.setFloat(16, moe.getHealth());
            statement.setFloat(17, moe.getFoodLevel());
            statement.setFloat(18, moe.getExhaustion());
            statement.setFloat(19, moe.getSaturation());
            statement.setFloat(20, moe.getStress());
            statement.setFloat(21, moe.getRelaxation());
            statement.setFloat(22, moe.getLoyalty());
            statement.setFloat(23, moe.getAffection());
            statement.setFloat(24, moe.getSlouch());
            statement.setFloat(25, moe.getAge());
            statement.setLong(26, moe.getLastSeen());
            statement.setBoolean(27, moe.hasHome());
            bindPosition(statement, 28, moe.getHome().getDim(), moe.getHome().getPos());
            statement.setLong(32, this.databaseId);
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    public void markHiding(BlockPartyDB db, ServerLevel level, BlockPos hiddenPos, BlockState hiddenState) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE NPCs
                SET Hiding = 1, BlockState = ?, VisibleBlockState = ?,
                    HiddenPosDim = ?, HiddenPosX = ?, HiddenPosY = ?, HiddenPosZ = ?
                WHERE DatabaseID = ?;
                """)) {
            statement.setInt(1, Block.getId(hiddenState));
            statement.setInt(2, Block.getId(hiddenState));
            bindPosition(statement, 3, level.dimension(), hiddenPos);
            statement.setLong(7, this.databaseId);
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

    public static void updateHealth(BlockPartyDB db, long id, float health) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("UPDATE NPCs SET Health = ? WHERE DatabaseID = ?;")) {
            statement.setFloat(1, health);
            statement.setLong(2, id);
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    public static void updateFood(BlockPartyDB db, long id, float foodLevel, float exhaustion, float saturation) throws SQLException {
        Connection connection = db.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                UPDATE NPCs
                SET FoodLevel = ?, Exhaustion = ?, Saturation = ?
                WHERE DatabaseID = ?;
                """)) {
            statement.setFloat(1, foodLevel);
            statement.setFloat(2, exhaustion);
            statement.setFloat(3, saturation);
            statement.setLong(4, id);
            statement.executeUpdate();
        } finally {
            db.free(connection);
        }
    }

    public void applyTo(Moe moe) {
        moe.setDatabaseID(this.databaseId);
        moe.setOwnerUUID(this.playerUuid);
        moe.setBlockStateFromRow(this.blockState);
        moe.setVisibleBlockState(this.visibleBlockState);
        moe.setGivenName(this.name);
        moe.setGender(this.gender);
        moe.setBloodType(this.bloodType);
        moe.setDere(this.dere);
        moe.setZodiac(this.zodiac);
        moe.setEmotion(this.emotion);
        moe.setMoeScale(this.scale);
        moe.setCorporeal(this.corporeal);
        moe.setHealth(this.health);
        moe.setFoodLevel(this.foodLevel);
        moe.setExhaustion(this.exhaustion);
        moe.setSaturation(this.saturation);
        moe.setStress(this.stress);
        moe.setRelaxation(this.relaxation);
        moe.setLoyalty(this.loyalty);
        moe.setAffection(this.affection);
        moe.setSlouch(this.slouch);
        moe.setAge(this.age);
        moe.setLastSeen(this.lastSeenAt);
        moe.setHasHome(this.hasHome);
        moe.setHome(this.home);
    }

    private static NPC read(ResultSet result) throws SQLException {
        return new NPC(
                result.getLong("DatabaseID"),
                parseDimension(result.getString("PosDim")),
                new BlockPos(result.getInt("PosX"), result.getInt("PosY"), result.getInt("PosZ")),
                parseUuid(result.getString("PlayerUUID")),
                result.getBoolean("Dead"),
                result.getString("Name"),
                result.getString("Gender"),
                result.getString("BloodType"),
                result.getString("Dere"),
                result.getString("Zodiac"),
                result.getString("Emotion"),
                Block.stateById(result.getInt("BlockState")),
                Block.stateById(result.getInt("VisibleBlockState")),
                result.getFloat("Scale"),
                result.getBoolean("IsCorporeal"),
                result.getFloat("Health"),
                result.getFloat("FoodLevel"),
                result.getFloat("Exhaustion"),
                result.getFloat("Saturation"),
                result.getFloat("Stress"),
                result.getFloat("Relaxation"),
                result.getFloat("Loyalty"),
                result.getFloat("Affection"),
                result.getFloat("Slouch"),
                result.getFloat("Age"),
                result.getLong("LastSeenAt"),
                result.getBoolean("HasHome"),
                new DimBlockPos(parseDimension(result.getString("HomePosDim")),
                        new BlockPos(result.getInt("HomePosX"), result.getInt("HomePosY"), result.getInt("HomePosZ"))),
                result.getBoolean("Hiding"),
                new BlockPos(result.getInt("HiddenPosX"), result.getInt("HiddenPosY"), result.getInt("HiddenPosZ")));
    }

    private static UUID parseUuid(String value) {
        return value == null || value.isBlank() ? EMPTY_UUID : UUID.fromString(value);
    }

    private static void applyPersistentString(CompoundTag data, String key, java.util.function.Consumer<String> setter) {
        if (data.contains(key)) {
            setter.accept(data.getString(key));
        }
    }

    private static ResourceKey<Level> parseDimension(String value) {
        if (value == null || value.isBlank()) {
            return Level.OVERWORLD;
        }
        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(value));
    }

    private static void bindPosition(PreparedStatement statement, int start, ResourceKey<Level> dimension, BlockPos pos) throws SQLException {
        statement.setString(start, dimension.location().toString());
        statement.setInt(start + 1, pos.getX());
        statement.setInt(start + 2, pos.getY());
        statement.setInt(start + 3, pos.getZ());
    }
}
