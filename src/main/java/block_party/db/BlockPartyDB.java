package block_party.db;

import block_party.blocks.entity.AbstractDataBlockEntity;
import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.db.records.AttentionRecord;
import block_party.db.records.Garden;
import block_party.db.records.Location;
import block_party.db.records.NPC;
import block_party.db.records.PlayerRelationship;
import block_party.db.records.Sapling;
import block_party.db.records.Shrine;
import block_party.db.records.TsukumogamiCandidate;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
            PlayerRelationship.createTable(data);
            createDataBlockTables(data);
            data.seedRelationshipsFromOwnerList();
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
                    CREATE TABLE IF NOT EXISTS TsukumogamiCandidates (
                        DatabaseID INTEGER PRIMARY KEY AUTOINCREMENT,
                        PosDim TEXT NOT NULL DEFAULT 'minecraft:overworld',
                        PosX INTEGER NOT NULL DEFAULT 0,
                        PosY INTEGER NOT NULL DEFAULT 0,
                        PosZ INTEGER NOT NULL DEFAULT 0,
                        PlayerUUID TEXT NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
                        BlockState INTEGER NOT NULL DEFAULT 0,
                        TileEntityData TEXT NOT NULL DEFAULT '{}',
                        CreatedGameTime INTEGER NOT NULL DEFAULT 0,
                        MatureAtGameTime INTEGER NOT NULL DEFAULT 0,
                        ShrineDatabaseID INTEGER NOT NULL DEFAULT 0,
                        UNIQUE(PosDim, PosX, PosY, PosZ)
                    );
                    """);
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
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS AttentionRecords (
                        DatabaseID INTEGER PRIMARY KEY AUTOINCREMENT,
                        PlayerUUID TEXT NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000',
                        Type TEXT NOT NULL DEFAULT '',
                        Source TEXT NOT NULL DEFAULT '',
                        PosDim TEXT NOT NULL DEFAULT 'minecraft:overworld',
                        PosX INTEGER NOT NULL DEFAULT 0,
                        PosY INTEGER NOT NULL DEFAULT 0,
                        PosZ INTEGER NOT NULL DEFAULT 0,
                        BlockState INTEGER NOT NULL DEFAULT 0,
                        ItemID TEXT NOT NULL DEFAULT '',
                        ItemCount INTEGER NOT NULL DEFAULT 0,
                        Count INTEGER NOT NULL DEFAULT 0,
                        FirstGameTime INTEGER NOT NULL DEFAULT 0,
                        LastGameTime INTEGER NOT NULL DEFAULT 0,
                        UNIQUE(PlayerUUID, Type, Source, ItemID)
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

    @Deprecated
    public void addTo(UUID player, long id) {
        this.addPlayerNpc(player, id);
    }

    public void addPlayerNpc(UUID player, long id) {
        List<Long> ids = this.byPlayer.computeIfAbsent(player, ignored -> new ArrayList<>());
        if (!ids.contains(id)) {
            ids.add(id);
        }
        this.seedLegacyRelationship(player, id);
        this.setDirty();
    }

    public List<Long> getFrom(UUID player) {
        return List.copyOf(this.byPlayer.getOrDefault(player, List.of()));
    }

    public List<Long> listNpcIds(UUID player) {
        return this.listPlayerNpcIds(player);
    }

    public List<Long> listPlayerNpcIds(UUID player) {
        List<Long> visible = new ArrayList<>();
        for (long id : this.byPlayer.getOrDefault(player, List.of())) {
            if (this.loadPlayerNpc(player, id).isPresent()) {
                visible.add(id);
            }
        }
        return List.copyOf(visible);
    }

    public List<Long> listYearbookNpcIds(UUID player) {
        List<Long> visible = new ArrayList<>();
        for (long id : this.relationshipNpcIds(player, PlayerRelationship::listYearbookNpcIds)) {
            if (this.loadYearbookNpc(player, id).isPresent()) {
                visible.add(id);
            }
        }
        return List.copyOf(visible);
    }

    public List<Long> listPhoneContactNpcIds(UUID player) {
        List<Long> visible = new ArrayList<>();
        for (long id : this.relationshipNpcIds(player, PlayerRelationship::listPhoneContactNpcIds)) {
            if (this.loadPhoneContactNpc(player, id).isPresent()) {
                visible.add(id);
            }
        }
        return List.copyOf(visible);
    }

    @Deprecated
    public java.util.Optional<NPC> loadOwnedNpc(UUID player, long id) {
        return this.loadPlayerNpc(player, id);
    }

    public java.util.Optional<NPC> loadPlayerNpc(UUID player, long id) {
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

    public java.util.Optional<NPC> loadYearbookNpc(UUID player, long id) {
        if (!this.hasYearbookPage(player, id)) {
            return java.util.Optional.empty();
        }
        return this.findNpcSafe(id);
    }

    public java.util.Optional<NPC> loadRelatedNpc(UUID player, long id) {
        if (!this.hasPlayerRelationship(player, id)) {
            return java.util.Optional.empty();
        }
        return this.findNpcSafe(id);
    }

    public java.util.Optional<NPC> loadPhoneContactNpc(UUID player, long id) {
        if (!this.hasPhoneContact(player, id)) {
            return java.util.Optional.empty();
        }
        return this.findNpcSafe(id);
    }

    @Deprecated
    public boolean removeOwnedNpc(UUID player, long id) {
        return this.removeYearbookPage(player, id);
    }

    public boolean removeYearbookPage(UUID player, long id) {
        if (this.loadYearbookNpc(player, id).isEmpty()) {
            return false;
        }
        List<Long> ids = this.byPlayer.get(player);
        boolean removedLegacyEntry = ids != null && ids.remove(id);
        this.setYearbookSignedSafe(player, id, false);
        if (removedLegacyEntry) {
            this.setDirty();
        }
        return true;
    }

    @Deprecated
    public java.util.Optional<Moe> callOwnedNpc(ServerPlayer player, long id) {
        return this.callPhoneContactNpc(player, id);
    }

    public java.util.Optional<Moe> callPhoneContactNpc(ServerPlayer player, long id) {
        if (!(player.level() instanceof ServerLevel level)) {
            return java.util.Optional.empty();
        }
        return this.callPhoneContactNpc(level, player.getUUID(), player.position(), player.getYRot(), id);
    }

    @Deprecated
    public java.util.Optional<Moe> findOwnedLoadedMoe(ServerLevel level, UUID player, long id) {
        return this.findPlayerLoadedMoe(level, player, id);
    }

    public java.util.Optional<Moe> findPlayerLoadedMoe(ServerLevel level, UUID player, long id) {
        java.util.Optional<NPC> row = this.loadPlayerNpc(player, id);
        if (row.isEmpty() || row.get().hiding()) {
            return java.util.Optional.empty();
        }
        ServerLevel npcLevel = level.getServer().getLevel(row.get().dimension());
        if (npcLevel != level) {
            return java.util.Optional.empty();
        }
        return CellPhone.findLoadedMoe(level, id);
    }

    public java.util.Optional<Moe> findRelatedLoadedMoe(ServerLevel level, UUID player, long id) {
        java.util.Optional<NPC> row = this.loadRelatedNpc(player, id);
        if (row.isEmpty() || row.get().hiding()) {
            return java.util.Optional.empty();
        }
        ServerLevel npcLevel = level.getServer().getLevel(row.get().dimension());
        if (npcLevel != level) {
            return java.util.Optional.empty();
        }
        return CellPhone.findLoadedMoe(level, id);
    }

    @Deprecated
    public java.util.Optional<Moe> callOwnedNpc(ServerLevel callerLevel, UUID player, BlockPos callerPos, long id) {
        return this.callPhoneContactNpc(callerLevel, player, callerPos, id);
    }

    @Deprecated
    public java.util.Optional<Moe> callOwnedNpc(ServerLevel callerLevel, UUID player, Vec3 callerPos, float callerYRot, long id) {
        return this.callPhoneContactNpc(callerLevel, player, callerPos, callerYRot, id);
    }

    public java.util.Optional<Moe> callPhoneContactNpc(ServerLevel callerLevel, UUID player, BlockPos callerPos, long id) {
        return this.callPhoneContactNpc(callerLevel, player, Vec3.atBottomCenterOf(callerPos), -90.0F, id);
    }

    public java.util.Optional<Moe> callPhoneContactNpc(ServerLevel callerLevel, UUID player, Vec3 callerPos, float callerYRot, long id) {
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

    public java.util.Optional<NPC> findCardinalNpc(net.minecraft.world.level.block.state.BlockState visibleBlockState) throws SQLException {
        return NPC.findCardinalNpc(this, visibleBlockState);
    }

    public PlayerRelationship ensurePlayerRelationship(long npcId, UUID player) throws SQLException {
        return PlayerRelationship.ensure(this, npcId, player);
    }

    public java.util.Optional<PlayerRelationship> findPlayerRelationship(long npcId, UUID player) throws SQLException {
        return PlayerRelationship.find(this, npcId, player);
    }

    public java.util.Optional<PlayerRelationship> findPlayerRelationshipSafe(long npcId, UUID player) {
        try {
            return this.findPlayerRelationship(npcId, player);
        } catch (RuntimeException | SQLException exception) {
            return java.util.Optional.empty();
        }
    }

    public boolean hasPlayerRelationship(UUID player, long npcId) {
        try {
            return PlayerRelationship.find(this, npcId, player).isPresent();
        } catch (RuntimeException | SQLException exception) {
            return this.byPlayer.getOrDefault(player, List.of()).contains(npcId);
        }
    }

    public void setYearbookSigned(long npcId, UUID player, boolean signed) throws SQLException {
        PlayerRelationship.setYearbookSigned(this, npcId, player, signed);
    }

    public void setPhoneContact(long npcId, UUID player, boolean contact) throws SQLException {
        PlayerRelationship.setPhoneContact(this, npcId, player, contact);
    }

    public void setPlayerFeelings(long npcId, UUID player, float affection, float loyalty) throws SQLException {
        PlayerRelationship.setFeelings(this, npcId, player, affection, loyalty);
    }

    public void deleteNpc(long id) throws SQLException {
        NPC.delete(this, id);
    }

    public void upsertDataBlock(AbstractDataBlockEntity entity) throws SQLException {
        String table = entity.getTableName();
        if ("NPCs".equals(table)) {
            if (entity instanceof ShimenawaBlockEntity shimenawa && entity.getLevel() instanceof ServerLevel level) {
                NPC row = NPC.createFromShimenawa(this, level, shimenawa);
                this.addPlayerNpc(row.playerUuid(), row.databaseId());
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

    public void recordAttention(ServerLevel level, UUID player, String type, String source, BlockPos pos, BlockState state, String itemId, int itemCount, long gameTime) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO AttentionRecords (
                    PlayerUUID, Type, Source, PosDim, PosX, PosY, PosZ, BlockState, ItemID, ItemCount, Count, FirstGameTime, LastGameTime
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, ?, ?)
                ON CONFLICT(PlayerUUID, Type, Source, ItemID) DO UPDATE SET
                    PosDim = excluded.PosDim,
                    PosX = excluded.PosX,
                    PosY = excluded.PosY,
                    PosZ = excluded.PosZ,
                    BlockState = excluded.BlockState,
                    ItemCount = excluded.ItemCount,
                    Count = AttentionRecords.Count + 1,
                    LastGameTime = excluded.LastGameTime;
                """)) {
            statement.setString(1, player.toString());
            statement.setString(2, normalizeAttentionKey(type));
            statement.setString(3, normalizeAttentionKey(source));
            statement.setString(4, level.dimension().location().toString());
            statement.setInt(5, pos.getX());
            statement.setInt(6, pos.getY());
            statement.setInt(7, pos.getZ());
            statement.setInt(8, Block.getId(state));
            statement.setString(9, normalizeAttentionKey(itemId));
            statement.setInt(10, Math.max(0, itemCount));
            statement.setLong(11, gameTime);
            statement.setLong(12, gameTime);
            statement.executeUpdate();
        } finally {
            this.free(connection);
        }
    }

    public java.util.Optional<AttentionRecord> latestAttention(UUID player) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT DatabaseID, PlayerUUID, Type, Source, PosDim, PosX, PosY, PosZ, BlockState, ItemID, ItemCount, Count, FirstGameTime, LastGameTime
                FROM AttentionRecords
                WHERE PlayerUUID = ?
                ORDER BY LastGameTime DESC, DatabaseID DESC
                LIMIT 1;
                """)) {
            statement.setString(1, player.toString());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? java.util.Optional.of(readAttention(result)) : java.util.Optional.empty();
            }
        } finally {
            this.free(connection);
        }
    }

    public java.util.Optional<AttentionRecord> findAttention(UUID player, String type, String source) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT DatabaseID, PlayerUUID, Type, Source, PosDim, PosX, PosY, PosZ, BlockState, ItemID, ItemCount, Count, FirstGameTime, LastGameTime
                FROM AttentionRecords
                WHERE PlayerUUID = ? AND Type = ? AND Source = ?
                ORDER BY LastGameTime DESC, DatabaseID DESC
                LIMIT 1;
                """)) {
            statement.setString(1, player.toString());
            statement.setString(2, normalizeAttentionKey(type));
            statement.setString(3, normalizeAttentionKey(source));
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? java.util.Optional.of(readAttention(result)) : java.util.Optional.empty();
            }
        } finally {
            this.free(connection);
        }
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

    public List<Shrine> listShrineRows(ResourceKey<Level> dimension) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID FROM Shrines
                WHERE PosDim = ?
                ORDER BY DatabaseID ASC;
                """)) {
            statement.setString(1, dimension.location().toString());
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

    public void upsertTsukumogamiCandidate(ServerLevel level, BlockPos pos, UUID player, BlockState state,
                                           CompoundTag tileEntityData, long createdGameTime, long matureAtGameTime,
                                           long shrineDatabaseId) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                INSERT INTO TsukumogamiCandidates (
                    PosDim, PosX, PosY, PosZ, PlayerUUID, BlockState, TileEntityData,
                    CreatedGameTime, MatureAtGameTime, ShrineDatabaseID
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(PosDim, PosX, PosY, PosZ) DO UPDATE SET
                    PlayerUUID = excluded.PlayerUUID,
                    BlockState = excluded.BlockState,
                    TileEntityData = excluded.TileEntityData,
                    CreatedGameTime = excluded.CreatedGameTime,
                    MatureAtGameTime = excluded.MatureAtGameTime,
                    ShrineDatabaseID = excluded.ShrineDatabaseID;
                """)) {
            statement.setString(1, level.dimension().location().toString());
            statement.setInt(2, pos.getX());
            statement.setInt(3, pos.getY());
            statement.setInt(4, pos.getZ());
            statement.setString(5, player.toString());
            statement.setInt(6, Block.getId(state));
            statement.setString(7, tileEntityData == null ? "{}" : tileEntityData.toString());
            statement.setLong(8, createdGameTime);
            statement.setLong(9, matureAtGameTime);
            statement.setLong(10, shrineDatabaseId);
            statement.executeUpdate();
        } finally {
            this.free(connection);
        }
    }

    public List<TsukumogamiCandidate> listMatureTsukumogamiCandidates(long gameTime) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID, BlockState, TileEntityData,
                       CreatedGameTime, MatureAtGameTime, ShrineDatabaseID
                FROM TsukumogamiCandidates
                WHERE MatureAtGameTime <= ?
                ORDER BY MatureAtGameTime ASC, DatabaseID ASC;
                """)) {
            statement.setLong(1, gameTime);
            List<TsukumogamiCandidate> rows = new ArrayList<>();
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    rows.add(readTsukumogamiCandidate(result));
                }
            }
            return List.copyOf(rows);
        } finally {
            this.free(connection);
        }
    }

    public java.util.Optional<TsukumogamiCandidate> findTsukumogamiCandidate(ServerLevel level, BlockPos pos) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                SELECT DatabaseID, PosDim, PosX, PosY, PosZ, PlayerUUID, BlockState, TileEntityData,
                       CreatedGameTime, MatureAtGameTime, ShrineDatabaseID
                FROM TsukumogamiCandidates
                WHERE PosDim = ? AND PosX = ? AND PosY = ? AND PosZ = ?
                LIMIT 1;
                """)) {
            statement.setString(1, level.dimension().location().toString());
            statement.setInt(2, pos.getX());
            statement.setInt(3, pos.getY());
            statement.setInt(4, pos.getZ());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? java.util.Optional.of(readTsukumogamiCandidate(result)) : java.util.Optional.empty();
            }
        } finally {
            this.free(connection);
        }
    }

    public void deleteTsukumogamiCandidate(long id) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM TsukumogamiCandidates WHERE DatabaseID = ?;")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } finally {
            this.free(connection);
        }
    }

    public void deleteTsukumogamiCandidate(ServerLevel level, BlockPos pos) throws SQLException {
        Connection connection = this.openConnection();
        try (PreparedStatement statement = connection.prepareStatement("""
                DELETE FROM TsukumogamiCandidates
                WHERE PosDim = ? AND PosX = ? AND PosY = ? AND PosZ = ?;
                """)) {
            statement.setString(1, level.dimension().location().toString());
            statement.setInt(2, pos.getX());
            statement.setInt(3, pos.getY());
            statement.setInt(4, pos.getZ());
            statement.executeUpdate();
        } finally {
            this.free(connection);
        }
    }

    private void seedRelationshipsFromOwnerList() throws SQLException {
        for (Map.Entry<UUID, List<Long>> entry : this.byPlayer.entrySet()) {
            for (long id : entry.getValue()) {
                this.seedLegacyRelationshipOrThrow(entry.getKey(), id);
            }
        }
    }

    private void seedLegacyRelationship(UUID player, long id) {
        try {
            this.seedLegacyRelationshipOrThrow(player, id);
        } catch (RuntimeException | SQLException ignored) {
        }
    }

    private void seedLegacyRelationshipOrThrow(UUID player, long id) throws SQLException {
        java.util.Optional<PlayerRelationship> existing = PlayerRelationship.find(this, id, player);
        PlayerRelationship.setYearbookSigned(this, id, player, true);
        PlayerRelationship.setPhoneContact(this, id, player, true);
        PlayerRelationship relationship = existing.orElseGet(() -> this.findPlayerRelationshipSafe(id, player).orElse(null));
        if (relationship == null || (relationship.affection() == 0.0F && relationship.loyalty() == 0.0F)) {
            java.util.Optional<NPC> row = this.findNpc(id);
            if (row.isPresent()) {
                PlayerRelationship.setFeelings(this, id, player, row.get().affection(), row.get().loyalty());
            }
        }
    }

    private List<Long> relationshipNpcIds(UUID player, RelationshipListQuery query) {
        try {
            return query.list(this, player);
        } catch (RuntimeException | SQLException exception) {
            return this.byPlayer.getOrDefault(player, List.of());
        }
    }

    private boolean hasYearbookPage(UUID player, long id) {
        try {
            return PlayerRelationship.find(this, id, player)
                    .map(PlayerRelationship::yearbookSigned)
                    .orElse(false);
        } catch (RuntimeException | SQLException exception) {
            return this.byPlayer.getOrDefault(player, List.of()).contains(id);
        }
    }

    private boolean hasPhoneContact(UUID player, long id) {
        try {
            return PlayerRelationship.find(this, id, player)
                    .map(PlayerRelationship::phoneContact)
                    .orElse(false);
        } catch (RuntimeException | SQLException exception) {
            return this.byPlayer.getOrDefault(player, List.of()).contains(id);
        }
    }

    private void setYearbookSignedSafe(UUID player, long id, boolean signed) {
        try {
            PlayerRelationship.setYearbookSigned(this, id, player, signed);
        } catch (RuntimeException | SQLException ignored) {
        }
    }

    @FunctionalInterface
    private interface RelationshipListQuery {
        List<Long> list(BlockPartyDB db, UUID player) throws SQLException;
    }

    private static Location readLocation(ResultSet result) throws SQLException {
        return new Location(
                result.getLong("DatabaseID"),
                readDimBlockPos(result),
                readUuid(result, "PlayerUUID"),
                result.getString("RequiredCondition"),
                result.getInt("Priority"));
    }

    private static TsukumogamiCandidate readTsukumogamiCandidate(ResultSet result) throws SQLException {
        CompoundTag tileEntityData = new CompoundTag();
        try {
            tileEntityData = net.minecraft.nbt.TagParser.parseTag(result.getString("TileEntityData"));
        } catch (Exception ignored) {
        }
        return new TsukumogamiCandidate(
                result.getLong("DatabaseID"),
                readDimBlockPos(result),
                readUuid(result, "PlayerUUID"),
                Block.stateById(result.getInt("BlockState")),
                tileEntityData,
                result.getLong("CreatedGameTime"),
                result.getLong("MatureAtGameTime"),
                result.getLong("ShrineDatabaseID"));
    }

    private static AttentionRecord readAttention(ResultSet result) throws SQLException {
        return new AttentionRecord(
                result.getLong("DatabaseID"),
                readUuid(result, "PlayerUUID"),
                result.getString("Type"),
                result.getString("Source"),
                readDimBlockPos(result),
                Block.stateById(result.getInt("BlockState")),
                result.getString("ItemID"),
                result.getInt("ItemCount"),
                result.getInt("Count"),
                result.getLong("FirstGameTime"),
                result.getLong("LastGameTime"));
    }

    private static String normalizeAttentionKey(String value) {
        return value == null ? "" : value.trim().toLowerCase(java.util.Locale.ROOT);
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
