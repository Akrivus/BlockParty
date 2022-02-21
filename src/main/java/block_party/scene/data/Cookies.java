package block_party.scene.data;

import net.minecraft.nbt.CompoundTag;

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
