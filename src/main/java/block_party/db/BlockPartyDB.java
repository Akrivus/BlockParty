package block_party.db;

import block_party.client.ShrineLocation;
import block_party.db.records.*;
import block_party.messages.SShrineList;
import block_party.registry.CustomMessenger;
import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.compress.utils.Lists;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

@Mod.EventBusSubscriber
public class BlockPartyDB extends SavedData {
    public static Shrine.Schema Shrines = new Shrine.Schema();
    public static Location.Schema Locations = new Location.Schema();
    public static Garden.Schema Gardens = new Garden.Schema();
    public static Sapling.Schema Saplings = new Sapling.Schema();
    public static NPC.Schema NPCs = new NPC.Schema();
    public static String KEY = "blockparty_db";
    public static ShrineLocation ShrineLocation;
    public final List<String> names = Lists.newArrayList();
    private final Map<UUID, List<Long>> byPlayer = Maps.newHashMap();
    private final List<Connection> connections = Lists.newArrayList();
    private String database;

    public static BlockPartyDB load(CompoundTag compound) {
        BlockPartyDB data = new BlockPartyDB();
        compound.getList("Names", NBT.STRING).forEach((name) -> data.names.add(name.getAsString()));
        compound.getList("NPCsByPlayer", NBT.COMPOUND).forEach((nbt) -> {
            CompoundTag tag = (CompoundTag) nbt;
            List<Long> npcs = new ArrayList<>();
            tag.getList("NPCs", NBT.LONG).forEach((npc) -> npcs.add(((LongTag) npc).getAsLong()));
            data.byPlayer.put(UUID.fromString(tag.getString("Player")), npcs);
        });
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        ListTag names = new ListTag();
        this.names.forEach((name) -> names.add(StringTag.valueOf(name)));
        compound.put("Names", names);
        ListTag byPlayer = new ListTag();
        this.byPlayer.forEach((player, npcs) -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("Player", player.toString());
            ListTag list = new ListTag();
            npcs.forEach((npc) -> list.add(LongTag.valueOf(npc)));
            tag.put("NPCs", list);
            byPlayer.add(tag);
        });
        compound.put("NPCsByPlayer", byPlayer);
        return compound;
    }

    public List<Long> getFrom(Player player) {
        return this.byPlayer.getOrDefault(player.getUUID(), new ArrayList<>());
    }

    public void addTo(Player player, long id) {
        if (player == null) { return; }
        List<Long> list = this.getFrom(player);
        list.add(id);
        this.byPlayer.put(player.getUUID(), list);
        this.setDirty();
    }

    public void getDatabase(ServerLevel level) {
        File path = level.getServer().getWorldPath(new LevelResource("blockparty.db")).toFile();
        this.database = String.format("jdbc:sqlite:%s", path.getAbsolutePath());
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(this.database);
        this.connections.add(connection);
        return connection;
    }

    public List<Connection> getConnections() {
        return this.connections;
    }

    public void free(Connection connection) throws SQLException {
        connection.close();
        this.connections.remove(connection);
    }

    public static BlockPartyDB get(Level level) {
        try {
            ServerLevel server = level.getServer().getLevel(Level.OVERWORLD);
            DimensionDataStorage storage = server.getDataStorage();
            return storage.computeIfAbsent(BlockPartyDB::load, BlockPartyDB::new, KEY);
        } catch (NullPointerException e) {
            return new BlockPartyDB();
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load e) {
        if (e.getWorld() instanceof ServerLevel level) {
            try {
                Class.forName("org.sqlite.JDBC");
                get(level).getDatabase(level);
                Shrines.create(level);
                Locations.create(level);
                Gardens.create(level);
                NPCs.create(level);
            } catch (ClassNotFoundException x) {
                throw new ReportedException(new CrashReport("DB failed.", x));
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        if (e.getWorld() instanceof ServerLevel level) {
            get(level).getConnections().forEach((connection) -> {
                try {
                    connection.close();
                } catch (SQLException x) {
                    throw new ReportedException(new CrashReport("DB failed.", x));
                }
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        Player player = e.getPlayer();
        Level level = player.level;
        if (level.isClientSide()) { return; }
        CustomMessenger.send(player, new SShrineList(player, level.dimension()));
    }
}
