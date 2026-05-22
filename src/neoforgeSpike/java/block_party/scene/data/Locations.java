package block_party.scene.data;

import block_party.db.DimBlockPos;
import net.minecraft.nbt.CompoundTag;

public final class Locations extends AbstractVariables<DimBlockPos> {
    public Locations() {
    }

    public Locations(CompoundTag compound) {
        super(compound);
    }

    @Override
    protected DimBlockPos read(CompoundTag compound) {
        return new DimBlockPos(compound.getCompound("Value"));
    }

    @Override
    protected void write(CompoundTag compound, DimBlockPos value) {
        compound.put("Value", value.write());
    }

    @Override
    protected String getKey() {
        return "Locations";
    }
}
