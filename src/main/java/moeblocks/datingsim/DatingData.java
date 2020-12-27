package moeblocks.datingsim;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatingData extends WorldSavedData {
    private static final String KEY = "datingsim";
    private final Map<UUID, DatingSim> sims = new HashMap<>();

    @Override
    public void read(CompoundNBT compound) {
        ListNBT sims = compound.getList("DatingSims", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < sims.size(); ++i) {
            CompoundNBT nbt = sims.getCompound(i);
            this.sims.put(nbt.getUniqueId("UUID"), new DatingSim(nbt));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT sims = new ListNBT();
        this.sims.forEach((uuid, sim) -> sims.add(sim.write(new CompoundNBT())));
        compound.put("DatingSims", sims);
        return compound;
    }

    public static DatingSim get(World world, UUID uuid) {
        return DatingData.get(world).getSim(uuid);
    }

    public DatingSim getSim(UUID uuid) {
        if (!this.sims.containsKey(uuid)) { this.sims.put(uuid, new DatingSim(uuid)); }
        return this.sims.get(uuid);
    }

    public static DatingData get(World world) {
        if (world.isRemote()) { return new DatingData(KEY); }
        ServerWorld server = world.getServer().getWorld(World.OVERWORLD);
        DimensionSavedDataManager storage = server.getSavedData();
        return storage.getOrCreate(DatingData::new, KEY);
    }

    public DatingData() {
        this(KEY);
    }

    public DatingData(String name) {
        super(name);
    }
}
