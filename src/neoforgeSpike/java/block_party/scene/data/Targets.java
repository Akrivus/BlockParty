package block_party.scene.data;

import net.minecraft.nbt.CompoundTag;

public final class Targets extends AbstractVariables<Integer> {
    public Targets() {
    }

    public Targets(CompoundTag compound) {
        super(compound);
    }

    @Override
    protected Integer read(CompoundTag compound) {
        return compound.getInt("Value");
    }

    @Override
    protected void write(CompoundTag compound, Integer value) {
        compound.putInt("Value", value);
    }

    @Override
    protected String getKey() {
        return "Targets";
    }
}
