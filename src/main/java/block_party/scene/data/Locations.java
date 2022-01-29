package block_party.scene.data;

import block_party.db.DimBlockPos;
import net.minecraft.nbt.CompoundTag;

public class Locations extends AbstractVariables<DimBlockPos> {
    public Locations(CompoundTag compound) {
        super(compound);
    }

    public Locations() {
        super();
    }

    @Override
    public String getKey() {
        return "Locations";
    }

    @Override
    public DimBlockPos read(CompoundTag compound) {
        return new DimBlockPos(compound.getCompound("Value"));
    }

    @Override
    public CompoundTag write(CompoundTag compound, DimBlockPos value) {
        compound.put("Value", value.write());
        return compound;
    }
}
