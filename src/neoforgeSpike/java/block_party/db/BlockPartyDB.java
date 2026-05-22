package block_party.db;

import block_party.db.records.NPC;
import block_party.entities.Moe;
import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
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
        } catch (ReflectiveOperationException | SQLException exception) {
            throw new IllegalStateException("Block Party SQLite bootstrap failed", exception);
        }
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
        return this.callOwnedNpc(level, player.getUUID(), player.blockPosition(), id);
    }

    public java.util.Optional<Moe> callOwnedNpc(ServerLevel level, UUID player, BlockPos callerPos, long id) {
        java.util.Optional<NPC> row = this.loadOwnedNpc(player, id);
        if (row.isEmpty() || row.get().hiding()) {
            return java.util.Optional.empty();
        }

        java.util.Optional<Moe> live = this.findLoadedMoe(level, id);
        if (live.isEmpty()) {
            return java.util.Optional.empty();
        }

        Moe moe = live.get();
        moe.moveToBlock(callerPos.east());
        moe.setFollowing(true);
        try {
            row.get().updateFromMoe(this, level, moe);
        } catch (SQLException exception) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of(moe);
    }

    private java.util.Optional<Moe> findLoadedMoe(ServerLevel level, long id) {
        for (Moe moe : level.getEntities(EntityTypeTest.forClass(Moe.class), moe ->
                moe.isAlive() && !moe.isRemoved() && moe.getDatabaseID() == id)) {
            return java.util.Optional.of(moe);
        }
        return java.util.Optional.empty();
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
}
