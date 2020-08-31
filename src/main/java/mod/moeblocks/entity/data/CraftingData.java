package mod.moeblocks.entity.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class CraftingData extends WorldSavedData {
    public HashMap<UUID, Integer> numberOfYearbooks = new HashMap<>();

    public CraftingData() {
        super("crafting_data");
    }

    public static CraftingData get(World world) {
        if (world instanceof ServerWorld) {
            ServerWorld server = world.getServer().getWorld(World.field_234918_g_);
            DimensionSavedDataManager storage = server.getSavedData();
            return storage.getOrCreate(CraftingData::new, "crafting_data");
        } else {
            return new CraftingData();
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        if (compound.contains("NumberOfYearbooks")) {
            CompoundNBT numberOfYearbooks = (CompoundNBT) compound.get("NumberOfYearbooks");
            Iterator<String> it = numberOfYearbooks.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                this.numberOfYearbooks.put(UUID.fromString(key), numberOfYearbooks.getInt(key));
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT numberOfYearbooks = new CompoundNBT();
        Iterator<UUID> it = this.numberOfYearbooks.keySet().iterator();
        while (it.hasNext()) {
            UUID key = it.next();
            numberOfYearbooks.putInt(key.toString(), this.numberOfYearbooks.get(key));
        }
        compound.put("NumberOfYearbooks", numberOfYearbooks);
        return compound;
    }

    public int getNumberOfYearbooks(UUID uuid) {
        return this.numberOfYearbooks.getOrDefault(uuid.toString(), 0);
    }

    public int getNumberOfYearbooks(PlayerEntity player) {
        return this.getNumberOfYearbooks(player.getUniqueID());
    }

    public int getYearbookEdition(UUID uuid) {
        int value = this.numberOfYearbooks.getOrDefault(uuid, 0) + 1;
        this.numberOfYearbooks.put(uuid, value);
        return value;
    }

    public int getYearbookEdition(PlayerEntity player) {
        return this.getYearbookEdition(player.getUniqueID());
    }
}
