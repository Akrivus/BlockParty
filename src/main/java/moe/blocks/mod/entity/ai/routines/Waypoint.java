package moe.blocks.mod.entity.ai.routines;

import moe.blocks.mod.entity.AbstractNPCEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.BlockPos;

public class Waypoint {
    protected final BlockPos pos;

    public Waypoint(BlockPos pos) {
        this.pos = pos;
    }

    public Waypoint(INBT compound) {
        this((CompoundNBT) compound);
    }

    public Waypoint(CompoundNBT compound) {
        this.pos = BlockPos.fromLong(compound.getLong("Position"));
    }

    public boolean matches(AbstractNPCEntity entity) {
        return this.pos.withinDistance(entity.getPosition(), 256);
    }

    public BlockPos getPosition() {
        return this.pos;
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.putLong("Position", this.pos.toLong());
        return compound;
    }
}
