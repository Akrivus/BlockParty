package block_party.scene.data;

import block_party.utils.NBT;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Map;

public class Cookies extends AbstractVariables<String> {
    public Cookies(CompoundTag compound) {
        super(compound);
    }

    public Cookies() {
        super();
    }

    @Override
    public String getKey() {
        return "Cookies";
    }

    @Override
    public String read(CompoundTag compound) {
        return compound.getString("Value");
    }

    @Override
    public CompoundTag write(CompoundTag compound, String value) {
        compound.putString("Value", value);
        return compound;
    }

    public void set(String key) { this.set(key, ""); }
}
