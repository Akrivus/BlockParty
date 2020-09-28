package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.partial.InteractEntity;
import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class SleepGoal extends Goal {
    private final NPCEntity entity;

    public SleepGoal(NPCEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.isSleeping() && !this.entity.world.isDaytime();
    }

    @Override
    public void resetTask() {
        this.entity.wakeUp();
    }

    @Override
    public void tick() {
        this.entity.startSleeping(this.entity.getBedPosition().get());
    }
}
