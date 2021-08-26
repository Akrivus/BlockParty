package block_party.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

public class DimBlockPos {
    private final ResourceKey<Level> dim;
    private ChunkPos chunk;
    private BlockPos pos;
    private boolean isEmpty;

    public DimBlockPos(CompoundTag compound) {
        this.dim = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(compound.getString("Dimension")));
        this.setPos(BlockPos.of(compound.getLong("Coordinates")));
        this.isEmpty = compound.getBoolean("IsEmpty");
    }

    public DimBlockPos(ResourceKey<Level> dim, BlockPos pos) {
        this.dim = dim;
        this.setPos(pos);
    }

    public DimBlockPos() {
        this.dim = Level.OVERWORLD;
        this.setPos(BlockPos.ZERO);
        this.isEmpty = true;
    }

    @Override
    public String toString() {
        return this.getDim().location().toString();
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setPos(BlockPos pos) {
        this.chunk = new ChunkPos(this.pos = pos);
        this.isEmpty = false;
    }

    public ResourceKey<Level> getDim() {
        return this.dim;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public CompoundTag write() {
        return this.write(new CompoundTag());
    }

    public CompoundTag write(CompoundTag compound) {
        compound.putString("Dimension", this.dim.location().toString());
        compound.putLong("Coordinates", this.pos.asLong());
        compound.putBoolean("IsEmpty", this.isEmpty);
        return compound;
    }

    public CompoundTag write(UUID uuid) {
        CompoundTag compound = new CompoundTag();
        compound.putString("UUID", uuid.toString());
        return this.write(compound);
    }

    public AABB getAABB() {
        double bX = this.chunk.getMinBlockX() - 1;
        double eX = bX + 16 + 1;
        double bY = 0;
        double eY = 255;
        double bZ = this.chunk.getMinBlockZ() - 1;
        double eZ = bZ + 16 + 1;
        return new AABB(bX, bY, bZ, eX, eY, eZ);
    }

    @Override
    public int hashCode() {
        return (int)(this.dim.toString().hashCode() + this.pos.asLong());
    }

    public ChunkPos getChunk() {
        return this.chunk;
    }

    public static DimBlockPos fromNBT(Tag nbt) {
        return new DimBlockPos((CompoundTag) nbt);
    }
}
