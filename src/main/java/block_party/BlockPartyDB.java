package block_party;

import block_party.db.records.*;
import block_party.init.BlockPartyMessages;
import block_party.message.SShrineList;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
    public List<String> names = new ArrayList<>();
    private final Map<UUID, List<UUID>> byPlayer = new HashMap<>();
    private final List<Connection> connections = new ArrayList<>();
    private String database;

    public static BlockPartyDB load(CompoundTag compound) {
        BlockPartyDB data = new BlockPartyDB();
        compound.getList("Names", Constants.NBT.TAG_STRING).forEach((name) -> data.names.add(name.getAsString()));
        compound.getList("NPCsByPlayer", Constants.NBT.TAG_COMPOUND).forEach((nbt) -> {
            CompoundTag tag = (CompoundTag) nbt;
            List<UUID> npcs = new ArrayList<>();
            tag.getList("NPCs", Constants.NBT.TAG_STRING).forEach((npc) -> npcs.add(UUID.fromString(npc.getAsString())));
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
            npcs.forEach((npc) -> list.add(StringTag.valueOf(npc.toString())));
            tag.put("NPCs", list);
            byPlayer.add(tag);
        });
        compound.put("NPCsByPlayer", byPlayer);
        return compound;
    }

    public List<UUID> getFrom(Player player) {
        return this.byPlayer.getOrDefault(player.getUUID(), new ArrayList<>());
    }

    public void addTo(Player player, UUID uuid) {
        if (player == null) { return; }
        List<UUID> list = this.getFrom(player);
        list.add(uuid);
        this.byPlayer.put(player.getUUID(), list);
        this.setDirty();
    }

    public String getDatabase(ServerLevel world) {
        File path = world.getServer().getWorldPath(new LevelResource("blockparty.db")).toFile();
        return this.database = String.format("jdbc:sqlite:%s", path.getAbsolutePath());
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

    public static BlockPartyDB get(Level world) {
        ServerLevel server = world.getServer().getLevel(Level.OVERWORLD);
        DimensionDataStorage storage = server.getDataStorage();
        return storage.computeIfAbsent(BlockPartyDB::load, BlockPartyDB::new, KEY);
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load e) {
        if (e.getWorld() instanceof ServerLevel) {
            ServerLevel world = (ServerLevel) e.getWorld();
            try {
                Class.forName("org.sqlite.JDBC");
                get(world).getDatabase(world);
                Shrines.create(world);
                Locations.create(world);
                Gardens.create(world);
                NPCs.create(world);
            } catch (ClassNotFoundException x) {
                throw new ReportedException(new CrashReport("DB failed.", x));
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        if (e.getWorld() instanceof ServerLevel) {
            ServerLevel world = (ServerLevel) e.getWorld();
            get(world).getConnections().forEach((connection) -> {
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
        Level world = e.getPlayer().level;
        if (world.isClientSide()) { return; }
        BlockPartyMessages.send(e.getPlayer(), new SShrineList(world.dimension()));
    }
}
