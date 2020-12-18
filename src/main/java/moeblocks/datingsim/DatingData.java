package moeblocks.datingsim;

import net.minecraft.entity.player.PlayerEntity;
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
    private Map<UUID, DatingSim> sims = new HashMap<>();

    public DatingData(String name) {
        super(name);
    }

    public DatingData() {
        this(KEY);
    }

    @Override
    public void read(CompoundNBT compound) {
        ListNBT games = compound.getList("DatingSims", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < games.size(); ++i) {
            CompoundNBT nbt = games.getCompound(i);
            this.sims.put(nbt.getUniqueId("UUID"), new DatingSim(nbt));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT games = new ListNBT();
        this.sims.forEach((uuid, game) -> games.add(game.write(new CompoundNBT())));
        compound.put("DatingSims", games);
        return compound;
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

    public static DatingSim get(World world, UUID uuid) {
        return DatingData.get(world).getSim(uuid);
    }
}
