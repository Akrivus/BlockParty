package block_party.db;

import block_party.blocks.entity.AbstractDataBlockEntity;
import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.db.records.Garden;
import block_party.db.records.Location;
import block_party.db.records.NPC;
import block_party.db.records.Sapling;
import block_party.db.records.Shrine;
import block_party.entities.Moe;
import block_party.utils.NBT;
import block_party.world.CellPhone;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public final class BlockPartyDB extends SavedData {
    public static final String KEY = "BlockParty_DB";
    public static final Factory<BlockPartyDB> FACTORY = new Factory<>(
            BlockPartyDB::new,
            BlockPartyDB::load);

    private final List<String> names = new ArrayList<>();
    private final Map<UUID, List<Long>> byPlayer = Maps.newHashMap();
    private final List<Connection> connections = new ArrayList<>();
    private String database;
    private static Driver sqliteDriver;

    public static void onServerStarted(ServerStartedEvent event) {
        bootstrap(event.getServer());
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        shutdown(event.getServer());
    }

    public static void bootstrap(MinecraftServer server) {
        try {
            sqliteDriver = loadSqliteDriver();
            BlockPartyDB data = get(server.overworld());
            data.configureDatabase(server);
            Connection connection = data.openConnection();
            data.free(connection);
            NPC.createTable(data);
            createDataBlockTables(data);
        } catch (ReflectiveOperationException | SQLException exception) {
            throw new IllegalStateException("Block Party SQLite bootstrap failed", exception);
        }
    }

    public static void createDataBlockTables(BlockPartyDB data) throws SQLException {
        Connection connection = data.openConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute(dataBlockTableSql("Shrines"));
            statement.execute(dataBlockTableSql("GardenLanterns"));
            statement.execute(dataBlockTableSql("Saplings"));
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS Locations (
                        DatabaseID INTEGER PRIMARY KEY,
                        PosDim TEXT NOT NULL DEFAULT 'minecraft:overworld',
                        PosX INTEGER NOT NULL DEFAULT 0,
                        PosY INTEGER NOT NULL DEFAULT 0,
                        PosZ INTEGER NOT NULL DEFAULT 0,
                        PlayerUUID TEXT NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
                        RequiredCondition TEXT NOT NULL DEFAULT 'ALWAYS',
                        Priority INTEGER NOT NULL DEFAULT 0
                    );
                    """);
        } finally {
            data.free(connection);
        }
    }

    private static String dataBlockTableSql(String tableName) {
        return """
                CREATE TABLE IF NOT EXISTS %s (
                    DatabaseID INTEGER PRIMARY KEY,
                    PosDim TEXT NOT NULL DEFAULT 'minecraft:overworld',
                    PosX INTEGER NOT NULL DEFAULT 0,
                    PosY INTEGER NOT NULL DEFAULT 0,
                    PosZ INTEGER NOT NULL DEFAULT 0,
                    PlayerUUID TEXT NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'
                );
                """.formatted(tableName);
    }

    private static Driver loadSqliteDriver() throws ReflectiveOperationException {
        try {
            return newSqliteDriver(Class.forName("org.sqlite.JDBC"));
        } catch (ClassNotFoundException first) {
            ClassLoader context = Thread.currentThread().getContextClassLoader();
            try {
                return newSqliteDriver(Class.forName("org.sqlite.JDBC", true, context));
            } catch (ClassNotFoundException second) {
                return newSqliteDriver(Class.forName("org.sqlite.JDBC", true, ClassLoader.getSystemClassLoader()));
            }
        }
    }

    private static Driver newSqliteDriver(Class<?> driverClass) throws ReflectiveOperationException {
        return (Driver) driverClass.getDeclaredConstructor().newInstance();
    }

    public static void shutdown(MinecraftServer server) {
        get(server.overworld()).closeConnections();
    }

    public static BlockPartyDB get(Level level) {
        ServerLevel overworld = level.getServer().getLevel(Level.OVERWORLD);
        DimensionDataStorage storage = overworld.getDataStorage();
        return storage.computeIfAbsent(FACTORY, KEY);
    }

    public static BlockPartyDB load(CompoundTag compound, HolderLookup.Provider provider) {
        BlockPartyDB data = new BlockPartyDB();
        compound.getList("Names", NBT.STRING).forEach(name -> data.names.add(name.getAsString()));
        compound.getList("NPCsByPlayer", NBT.COMPOUND).forEach(nbt -> {
            CompoundTag tag = (CompoundTag) nbt;
            List<Long> npcs = new ArrayList<>();
            tag.getList("NPCs", NBT.LONG).forEach(npc -> npcs.add(((LongTag) npc).getAsLong()));
            data.byPlayer.put(UUID.fromString(tag.getString("Player")), npcs);
        });
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider provider) {
        ListTag names = new ListTag();
        this.names.forEach(name -> names.add(StringTag.valueOf(name)));
        compound.put("Names", names);

        ListTag byPlayer = new ListTag();
        this.byPlayer.forEach((player, npcs) -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("Player", player.toString());
            ListTag list = new ListTag();
            npcs.forEach(npc -> list.add(LongTag.valueOf(npc)));
            tag.put("NPCs", list);
            byPlayer.add(tag);
        });
        compound.put("NPCsByPlayer", byPlayer);
        return compound;
    }

    public void addName(String name) {
        this.names.add(name);
        this.setDirty();
    }

    public List<String> names() {
        return List.copyOf(this.names);
    }

    public void addTo(UUID player, long id) {
        List<Long> ids = this.byPlayer.computeIfAbsent(player, ignored -> new ArrayList<>());
        if (!ids.contains(id)) {
            ids.add(id);
        }
        this.setDirty();
    }

    public List<Long> getFrom(UUID player) {
        return List.copyOf(this.byPlayer.getOrDefault(player, List.of()));
    }

    public List<Long> listNpcIds(UUID player) {
        List<Long> visible = new ArrayList<>();
        for (long id : this.byPlayer.getOrDefault(player, List.of())) {
            if (this.loadOwnedNpc(player, id).isPresent()) {
                visible.add(id);
            }
        }
        return List.copyOf(visible);
    }

    public java.util.Optional<NPC> loadOwnedNpc(UUID player, long id) {
        try {
            java.util.Optional<NPC> row = this.findNpc(id);
            if (row.isEmpty()) {
                return java.util.Optional.empty();
            }
            NPC npc = row.get();
            if (npc.dead() || !player.equals(npc.playerUuid())) {
                return java.util.Optional.empty();
            }
            return java.util.Optional.of(npc);
        } catch (RuntimeException | SQLException exception) {
            return java.util.Optional.empty();
        }
    }

    public boolean removeOwnedNpc(UUID player, long id) {
        if (this.loadOwnedNpc(player, id).isEmpty()) {
            return false;
        }
        List<Long> ids = this.byPlayer.get(player);
        if (ids == null) {
            return false;
        }
        boolean removed = ids.remove(id);
        if (removed) {
            this.setDirty();
        }
        return removed;
    }

    public java.util.Optional<Moe> callOwnedNpc(ServerPlayer player, long id) {
        if (!(player.level() instanceof ServerLevel level)) {
            return java.util.Optional.empty();
        }
        return this.callOwnedNpc(level, player.getUUID(), player.position(), player.getYRot(), id);
    }

    public java.util.Optional<Moe> findOwnedLoadedMoe(ServerLevel level, UUID player, long id) {
        java.util.Optional<NPC> row = this.loadOwnedNpc(player, id);
        if (row.isEmpty() || row.get().hiding()) {
            return java.util.Optional.empty();
        }
        ServerLevel npcLevel = level.getServer().getLevel(row.get().dimension());
        if (npcLevel != level) {
            return java.util.Optional.empty();
        }
        return CellPhone.findLoadedMoe(level, id);
    }

    public java.util.Optional<Moe> callOwnedNpc(ServerLevel callerLevel, UUID player, BlockPos callerPos, long id) {
        return this.callOwnedNpc(callerLevel, player, Vec3.atBottomCenterOf(callerPos), -90.0F, id);
    }

    public java.util.Optional<Moe> callOwnedNpc(ServerLevel callerLevel, UUID player, Vec3 callerPos, float callerYRot, long id) {
        return new CellPhone(this, callerLevel, player, callerPos, callerYRot, id).call();
    }

    public void configureDatabase(MinecraftServer server) {
        Path path = server.getWorldPath(new LevelResource("blockparty.db"));
        this.database = "jdbc:sqlite:" + path.toAbsolutePath();
    }

    public Connection openConnection() throws SQLException {
        if (this.database == null) {
            throw new SQLException("Block Party database path has not been configured");
        }
        if (sqliteDriver == null) {
            try {
                sqliteDriver = loadSqliteDriver();
            } catch (ReflectiveOperationException exception) {
                throw new SQLException("SQLite driver is not available", exception);
            }
        }
        Connection connection = sqliteDriver.connect(this.database, new Properties());
        if (connection == null) {
            throw new SQLException("SQLite driver rejected " + this.database);
        }
        this.connections.add(connection);
        return connection;
    }

    public void free(Connection connection) throws SQLException {
        connection.close();
        this.connections.remove(connection);
    }

    public void closeConnections() {
        List<Connection> open = List.copyOf(this.connections);
        this.connections.clear();
        for (Connection connection : open) {
            try {
                connection.close();
            } catch (SQLException exception) {
                throw new IllegalStateException("Block Party SQLite shutdown failed", exception);
            }
        }
    }

    public int openConnectionCount() {
        return this.connections.size();
    }

    public NPC createNpc(ServerLevel level, block_party.entities.Moe moe) throws SQLException {
        return NPC.create(this, level, moe);
    }

    public NPC createNpc(ServerLevel level, block_party.entities.Moe moe, long databaseId, boolean hiding, BlockPos hiddenPos) throws SQLException {
        return NPC.create(this, level, moe, databaseId, hiding, hiddenPos);
    }

    public java.util.Optional<NPC> findNpc(long id) throws SQLException {
        return NPC.find(this, id);
    }

    public java.util.Optional<NPC> findNpcSafe(long id) {
        try {
            return this.findNpc(id);
        } catch (RuntimeException | SQLException exception) {
            return java.util.Optional.empty();
        }
    }

    public void deleteNpc(long id) throws SQLException {
        NPC.delete(this, id);
    }

    public void upsertDataBlock(AbstractDataBlockEntity entity) throws SQLException {
        String table = entity.getTableName();
        if ("NPCs".equals(table)) {
            if (entity instanceof ShimenawaBlockEntity shimenawa && entity.getLevel() instanceof ServerLevel level) {
                NPC row = NPC.createFromShimenawa(this, level, shimenawa);
                this.addTo(row.playerUuid(), row.databaseId());
            }
            return;
        }

        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement(upsertSql(table, "Locations".equals(table)))) {
            statement.setLong(1, entity.getDatabaseID());
            statement.setString(2, entity.getDimBlockPos().getDim().location().toString());
            statement.setInt(3, entity.getBlockPos().getX());
            statement.setInt(4, entity.getBlockPos().getY());
            statement.setInt(5, entity.getBlockPos().getZ());
            statement.setString(6, entity.getPlayerUUID().toString());
            if ("Locations".equals(table)) {
                statement.setString(7, entity.getRequiredCondition());
                statement.setInt(8, entity.getPriority());
            }
            statement.executeUpdate();
        } finally {
            this.free(connection);
        }
    }

    private static String upsertSql(String table, boolean locative) {
        if (locative) {
            return """
                    INSERT INTO Locations (
                        DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID, RequiredCondition, Priority
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT(DatabaseID) DO UPDATE SET
                        PosDim = excluded.PosDim,
                        PosX = excluded.PosX,
                        PosY = excluded.PosY,
                        PosZ = excluded.PosZ,
                        PlayerUUID = excluded.PlayerUUID,
                        RequiredCondition = excluded.RequiredCondition,
                        Priority = excluded.Priority;
                    """;
        }
        return """
                INSERT INTO %s (
                    DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID
                ) VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT(DatabaseID) DO UPDATE SET
                    PosDim = excluded.PosDim,
                    PosX = excluded.PosX,
                    PosY = excluded.PosY,
                    PosZ = excluded.PosZ,
                    PlayerUUID = excluded.PlayerUUID;
                """.formatted(table);
    }

    public boolean dataBlockRowExists(String table, long id) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM " + table + " WHERE DatabaseID = ? LIMIT 1;")) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } finally {
            this.free(connection);
        }
    }

    public List<Garden> listGardens() throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID FROM GardenLanterns
                ORDER BY DatabaseID ASC;
                """)) {
            List<Garden> rows = new ArrayList<>();
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    rows.add(new Garden(
                            result.getLong("DatabaseID"),
                            readDimBlockPos(result),
                            readUuid(result, "PlayerUUID")));
                }
            }
            return List.copyOf(rows);
        } finally {
            this.free(connection);
        }
    }

    public List<Sapling> listSaplings() throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID FROM Saplings
                ORDER BY DatabaseID ASC;
                """)) {
            List<Sapling> rows = new ArrayList<>();
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    rows.add(new Sapling(
                            result.getLong("DatabaseID"),
                            readDimBlockPos(result),
                            readUuid(result, "PlayerUUID")));
                }
            }
            return List.copyOf(rows);
        } finally {
            this.free(connection);
        }
    }

    public List<Location> listLocations() throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID, RequiredCondition, Priority FROM Locations
                ORDER BY Priority DESC, DatabaseID ASC;
                """)) {
            List<Location> rows = new ArrayList<>();
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    rows.add(readLocation(result));
                }
            }
            return List.copyOf(rows);
        } finally {
            this.free(connection);
        }
    }

    public List<Location> listLocations(UUID player) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID, RequiredCondition, Priority FROM Locations
                WHERE PlayerUUID = ?
                ORDER BY Priority DESC, DatabaseID ASC;
                """)) {
            statement.setString(1, player.toString());
            List<Location> rows = new ArrayList<>();
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    rows.add(readLocation(result));
                }
            }
            return List.copyOf(rows);
        } finally {
            this.free(connection);
        }
    }

    public List<Shrine> listShrineRows(UUID player) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID FROM Shrines
                WHERE PlayerUUID = ?
                ORDER BY DatabaseID ASC;
                """)) {
            statement.setString(1, player.toString());
            List<Shrine> rows = new ArrayList<>();
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    rows.add(new Shrine(
                            result.getLong("DatabaseID"),
                            readDimBlockPos(result),
                            readUuid(result, "PlayerUUID")));
                }
            }
            return List.copyOf(rows);
        } finally {
            this.free(connection);
        }
    }

    public java.util.Optional<Shrine> findClosestShrine(UUID player, DimBlockPos origin) throws SQLException {
        return Shrine.closest(this.listShrineRows(player), origin);
    }

    public void deleteDataBlock(String table, long id) throws SQLException {
        if ("NPCs".equals(table)) {
            this.deleteNpc(id);
            return;
        }
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + table + " WHERE DatabaseID = ?;")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } finally {
            this.free(connection);
        }
    }

    public List<ShrineEntry> listShrines(UUID player, net.minecraft.resources.ResourceKey<Level> dimension) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT DatabaseID, PosX, PosY, PosZ FROM Shrines
                WHERE PlayerUUID = ? OR PosDim = ?
                ORDER BY DatabaseID ASC;
                """)) {
            statement.setString(1, player.toString());
            statement.setString(2, dimension.location().toString());
            List<ShrineEntry> shrines = new ArrayList<>();
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    shrines.add(new ShrineEntry(
                            result.getLong("DatabaseID"),
                            new BlockPos(result.getInt("PosX"), result.getInt("PosY"), result.getInt("PosZ"))));
                }
            }
            return List.copyOf(shrines);
        } finally {
            this.free(connection);
        }
    }

    public record ShrineEntry(long databaseId, BlockPos pos) {
    }

    private static Location readLocation(ResultSet result) throws SQLException {
        return new Location(
                result.getLong("DatabaseID"),
                readDimBlockPos(result),
                readUuid(result, "PlayerUUID"),
                result.getString("RequiredCondition"),
                result.getInt("Priority"));
    }

    private static DimBlockPos readDimBlockPos(ResultSet result) throws SQLException {
        return new DimBlockPos(
                parseDimension(result.getString("PosDim")),
                new BlockPos(result.getInt("PosX"), result.getInt("PosY"), result.getInt("PosZ")));
    }

    private static UUID readUuid(ResultSet result, String column) throws SQLException {
        String value = result.getString(column);
        return value == null || value.isBlank() ? AbstractDataBlockEntity.BLANK_UUID : UUID.fromString(value);
    }

    private static ResourceKey<Level> parseDimension(String value) {
        if (value == null || value.isBlank()) {
            return Level.OVERWORLD;
        }
        return ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(value));
    }
}
