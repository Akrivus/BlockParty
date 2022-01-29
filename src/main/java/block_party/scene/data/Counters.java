package block_party.scene.data;

import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Map;

public class Counters extends AbstractVariables<Integer> {
    public Counters(CompoundTag compound) {
        super(compound);
    }

    public Counters() {
        super();
    }

    @Override
    public String getKey() {
        return "Counters";
    }

    @Override
    public Integer read(CompoundTag compound) {
        return compound.getInt("Value");
    }

    @Override
    public CompoundTag write(CompoundTag compound, Integer value) {
        compound.putInt("Value", value);
        return compound;
    }

    public void increment(String key, int value) {
        this.set(key, this.get(key) + value);
    }

    public void decrement(String key, int value) {
        this.set(key, this.get(key) - value);
    }
}
