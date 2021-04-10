package moeblocks.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.UUID;

public class DimBlockPos {
    private final RegistryKey<World> dim;
    private ChunkPos chunk;
    private BlockPos pos;
    private boolean isEmpty;

    public DimBlockPos(CompoundNBT compound) {
        this.dim = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(compound.getString("Dimension")));
        this.setPos(BlockPos.fromLong(compound.getLong("Coordinates")));
        this.isEmpty = compound.getBoolean("IsEmpty");
    }

    public DimBlockPos(RegistryKey<World> dim, BlockPos pos) {
        this.dim = dim;
        this.setPos(pos);
    }

    public DimBlockPos() {
        this.dim = World.OVERWORLD;
        this.setPos(BlockPos.ZERO);
        this.isEmpty = true;
    }

    @Override
    public String toString() {
        return this.getDim().getLocation().toString();
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setPos(BlockPos pos) {
        this.chunk = new ChunkPos(this.pos = pos);
        this.isEmpty = false;
    }

    public RegistryKey<World> getDim() {
        return this.dim;
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    public CompoundNBT write() {
        return this.write(new CompoundNBT());
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.putString("Dimension", this.dim.getLocation().toString());
        compound.putLong("Coordinates", this.pos.toLong());
        compound.putBoolean("IsEmpty", this.isEmpty);
        return compound;
    }

    public CompoundNBT write(UUID uuid) {
        CompoundNBT compound = new CompoundNBT();
        compound.putString("UUID", uuid.toString());
        return this.write(compound);
    }

    public AxisAlignedBB getAABB() {
        double bX = this.chunk.getXStart() - 1;
        double eX = bX + 16 + 1;
        double bY = 0;
        double eY = 255;
        double bZ = this.chunk.getZStart() - 1;
        double eZ = bZ + 16 + 1;
        return new AxisAlignedBB(bX, bY, bZ, eX, eY, eZ);
    }

    @Override
    public int hashCode() {
        return (int)(this.dim.toString().hashCode() + this.pos.toLong());
    }

    public ChunkPos getChunk() {
        return this.chunk;
    }

    public static DimBlockPos fromNBT(INBT nbt) {
        return new DimBlockPos((CompoundNBT) nbt);
    }
}
