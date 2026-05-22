package block_party.db;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public final class DimBlockPos {
    private final ResourceKey<Level> dimension;
    private final BlockPos pos;
    private final boolean empty;

    public DimBlockPos() {
        this(Level.OVERWORLD, BlockPos.ZERO, true);
    }

    public DimBlockPos(CompoundTag compound) {
        this(
                ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(compound.getString("Dimension"))),
                BlockPos.of(compound.getLong("Coordinates")),
                compound.getBoolean("IsEmpty"));
    }

    public DimBlockPos(ResourceKey<Level> dimension, BlockPos pos) {
        this(dimension, pos, false);
    }

    private DimBlockPos(ResourceKey<Level> dimension, BlockPos pos, boolean empty) {
        this.dimension = dimension;
        this.pos = pos;
        this.empty = empty;
    }

    public ResourceKey<Level> getDim() {
        return this.dimension;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public CompoundTag write() {
        CompoundTag compound = new CompoundTag();
        compound.putLong("Coordinates", this.pos.asLong());
        compound.putString("Dimension", this.dimension.location().toString());
        compound.putBoolean("IsEmpty", this.empty);
        return compound;
    }
}
