package block_party.db;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

public class DimBlockPos extends BlockPos {
    private final ResourceKey<Level> dim;
    private boolean isEmpty;

    public DimBlockPos(CompoundTag compound) {
        super(BlockPos.of(compound.getLong("Coordinates")));
        this.dim = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compound.getString("Dimension")));
        this.isEmpty = compound.getBoolean("IsEmpty");
    }

    public DimBlockPos(ResourceKey<Level> dim, BlockPos pos) {
        super(pos);
        this.dim = dim;
    }

    public DimBlockPos() {
        super(BlockPos.ZERO);
        this.dim = Level.OVERWORLD;
        this.isEmpty = true;
    }

    @Override
    public int hashCode() {
        return (int) (this.dim.toString().hashCode() + this.asLong());
    }

    @Override
    public String toString() {
        return this.getDim().location().toString();
    }

    public ResourceKey<Level> getDim() {
        return this.dim;
    }

    public BlockPos getPos() {
        return new BlockPos(this.getX(), this.getY(), this.getZ());
    }

    public String getDimKey() {
        return this.dim.location().toString();
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public CompoundTag write() {
        return this.write(new CompoundTag());
    }

    public CompoundTag write(CompoundTag compound) {
        compound.putLong("Coordinates", this.asLong());
        compound.putString("Dimension", this.dim.location().toString());
        compound.putBoolean("IsEmpty", this.isEmpty);
        return compound;
    }

    public CompoundTag write(UUID uuid) {
        CompoundTag compound = new CompoundTag();
        compound.putString("UUID", uuid.toString());
        return this.write(compound);
    }

    public AABB getAABB() {
        double bX = this.getX() - 1;
        double eX = bX + 2;
        double bY = this.getY() - 1;
        double eY = bY + 2;
        double bZ = this.getZ() - 1;
        double eZ = bZ + 2;
        return new AABB(bX, bY, bZ, eX, eY, eZ);
    }

    public ChunkPos getChunk() {
        return new ChunkPos(this);
    }
}
