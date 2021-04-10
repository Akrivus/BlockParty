package moeblocks.init;

import moeblocks.data.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
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
    public static UUID GAME_UUID = UUID.randomUUID();
    public static String KEY = "moedata";
    public List<String> names = new ArrayList<>();
    public Map<UUID, List<UUID>> byPlayer = new HashMap<>();

    @Override
    public void read(CompoundNBT compound) {
        compound.getList("Names", Constants.NBT.TAG_STRING).forEach((name) -> this.names.add(name.getString()));
        compound.getList("MoesByPlayer", Constants.NBT.TAG_COMPOUND).forEach((nbt) -> {
            CompoundNBT tag = (CompoundNBT) nbt;
            List<UUID> moes = new ArrayList<>();
            compound.getList("Moes", Constants.NBT.TAG_STRING).forEach((moe) -> moes.add(UUID.fromString(moe.getString())));
            this.byPlayer.put(UUID.fromString(tag.getString("Player")), moes);
        });
        MoeData.GAME_UUID = compound.getUniqueId("GameUUID");
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
        compound.putUniqueId("GameUUID", MoeData.GAME_UUID);
        return compound;
    }

    public static MoeData get(World world) {
        ServerWorld server = world.getServer().getWorld(World.OVERWORLD);
        DimensionSavedDataManager storage = server.getSavedData();
        return storage.getOrCreate(MoeData::new, KEY);
    }

    public static UUID getGameUUID(World world) {
        MoeData.get(world);
        return GAME_UUID;
    }

    public MoeData() {
        this(KEY);
    }

    public MoeData(String name) {
        super(name);
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load e) {
        File databases = new File("./databases");
        if (!databases.exists()) { databases.mkdir(); }
        if (e.getWorld() instanceof ServerWorld) {
            MoeData.getGameUUID((ServerWorld)(e.getWorld()));
            ToriiGates.create(GAME_UUID);
            GardenLanterns.create(GAME_UUID);
            SakuraTrees.create(GAME_UUID);
            Shimenawa.create(GAME_UUID);
            HangingScrolls.create(GAME_UUID);
            LuckyCats.create(GAME_UUID);
            PaperLanterns.create(GAME_UUID);
            WindChimes.create(GAME_UUID);
            WritingTables.create(GAME_UUID);
            Moes.create(GAME_UUID);
        }
    }
}
