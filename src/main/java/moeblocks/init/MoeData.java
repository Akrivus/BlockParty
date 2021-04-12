package moeblocks.init;

import moeblocks.data.*;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

@Mod.EventBusSubscriber
public class MoeData extends WorldSavedData {
    public static ToriiGate.Schema ToriiGates = new ToriiGate.Schema();
    public static GardenLantern.Schema GardenLanterns = new GardenLantern.Schema();
    public static SakuraSapling.Schema SakuraTrees = new SakuraSapling.Schema();
    public static Shimenawa.Schema Shimenawa = new Shimenawa.Schema();
    public static HangingScroll.Schema HangingScrolls = new HangingScroll.Schema();
    public static LuckyCat.Schema LuckyCats = new LuckyCat.Schema();
    public static PaperLantern.Schema PaperLanterns = new PaperLantern.Schema();
    public static WindChimes.Schema WindChimes = new WindChimes.Schema();
    public static WritingTable.Schema WritingTables = new WritingTable.Schema();
    public static Moe.Schema Moes = new Moe.Schema();
    public static String KEY = "moedata";
    public List<String> names = new ArrayList<>();
    public Map<UUID, List<UUID>> byPlayer = new HashMap<>();
    private final List<Connection> connections = new ArrayList<>();
    private String database;

    @Override
    public void read(CompoundNBT compound) {
        compound.getList("Names", Constants.NBT.TAG_STRING).forEach((name) -> this.names.add(name.getString()));
        compound.getList("MoesByPlayer", Constants.NBT.TAG_COMPOUND).forEach((nbt) -> {
            CompoundNBT tag = (CompoundNBT) nbt;
            List<UUID> moes = new ArrayList<>();
            compound.getList("Moes", Constants.NBT.TAG_STRING).forEach((moe) -> moes.add(UUID.fromString(moe.getString())));
            this.byPlayer.put(UUID.fromString(tag.getString("Player")), moes);
        });
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT names = new ListNBT();
        this.names.forEach((name) -> names.add(StringNBT.valueOf(name)));
        compound.put("Names", names);
        CompoundNBT byPlayer = new CompoundNBT();
        this.byPlayer.forEach((player, moes) -> {
            ListNBT list = new ListNBT();
            moes.forEach((moe) -> list.add(StringNBT.valueOf(moe.toString())));
            byPlayer.put(player.toString(), list);
        });
        compound.put("MoesByPlayer", byPlayer);
        return compound;
    }

    public String getDatabase(ServerWorld world) {
        File path = world.getServer().func_240776_a_(new FolderName("moeblocks.db")).toFile();
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

    public static MoeData get(World world) {
        ServerWorld server = world.getServer().getWorld(World.OVERWORLD);
        DimensionSavedDataManager storage = server.getSavedData();
        return storage.getOrCreate(MoeData::new, KEY);
    }

    public MoeData() {
        this(KEY);
    }

    public MoeData(String name) {
        super(name);
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load e) {
        if (e.getWorld() instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) e.getWorld();
            try {
                Class.forName("org.sqlite.JDBC");
                MoeData.get(world).getDatabase(world);
                ToriiGates.create(world);
                GardenLanterns.create(world);
                SakuraTrees.create(world);
                Shimenawa.create(world);
                HangingScrolls.create(world);
                LuckyCats.create(world);
                PaperLanterns.create(world);
                WindChimes.create(world);
                WritingTables.create(world);
                Moes.create(world);
            } catch (ClassNotFoundException x) {
                throw new ReportedException(new CrashReport("DB failed.", x));
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        if (e.getWorld() instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) e.getWorld();
            MoeData.get(world).getConnections().forEach((connection) -> {
                try {
                    connection.close();
                } catch (SQLException x) {
                    throw new ReportedException(new CrashReport("DB failed.", x));
                }
            });
        }
    }
}
