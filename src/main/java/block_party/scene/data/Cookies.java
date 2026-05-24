package block_party.scene.data;

import net.minecraft.nbt.CompoundTag;

public final class Cookies extends AbstractVariables<String> {
    public Cookies() {
    }

    public Cookies(CompoundTag compound) {
        super(compound);
    }

    @Override
    protected String read(CompoundTag compound) {
        return compound.getString("Value");
    }

    @Override
    protected void write(CompoundTag compound, String value) {
        compound.putString("Value", value);
    }

    @Override
    protected String getKey() {
        return "Cookies";
    }
}
