package moe.blocks.mod.entity.ai.routines;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.BlockPos;

public class Waypoint {
    protected final Origin origin;
    protected final BlockPos pos;

    public Waypoint(BlockPos pos, Origin origin) {
        this.pos = pos;
        this.origin = origin;
    }

    public Waypoint(INBT compound) {
        this((CompoundNBT) compound);
    }

    public Waypoint(CompoundNBT compound) {
        this.pos = BlockPos.fromLong(compound.getLong("Position"));
        this.origin = Origin.valueOf(compound.getString("Origin"));
    }

    public CompoundNBT write(CompoundNBT compound) {
        compound.putLong("Position", this.pos.toLong());
        compound.putString("Origin", this.origin.name());
        return compound;
    }

    public enum Origin {
        PLAYER, ROUTINE, SOCIAL;
    }
}
