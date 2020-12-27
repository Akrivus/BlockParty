package moeblocks.util;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class DimBlockPos {
    public ChunkPos chunk;
    private RegistryKey<World> dim;
    private BlockPos pos;
    
    public DimBlockPos(CompoundNBT compound) {
        this.dim = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(compound.getString("Dimension")));
        this.setPos(BlockPos.fromLong(compound.getLong("Coordinates")));
    }
    
    public DimBlockPos(AbstractNPCEntity npc) {
        this.dim = npc.world.getDimensionKey();
        this.setPos(npc.getPosition());
    }
    
    public DimBlockPos(RegistryKey<World> dim, BlockPos pos) {
        this.dim = dim;
        this.setPos(pos);
    }
    
    @Override
    public String toString() {
        return String.join(" ", this.getDim().getLocation().getPath(), this.getPos().toString());
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public void setPos(BlockPos pos) {
        this.chunk = new ChunkPos(this.pos = pos);
    }
    
    public RegistryKey<World> getDim() {
        return this.dim;
    }
    
    public CompoundNBT write() {
        CompoundNBT compound = new CompoundNBT();
        compound.putString("Dimension", this.dim.getLocation().toString());
        compound.putLong("Coordinates", this.pos.toLong());
        return compound;
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
}
