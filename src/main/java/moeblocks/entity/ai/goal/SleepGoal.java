package moeblocks.entity.ai.goal;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SleepGoal extends Goal {
    private final AbstractNPCEntity entity;

    public SleepGoal(AbstractNPCEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.isSleeping() && !this.entity.isTimeToSleep();
    }

    @Override
    public void resetTask() {
        if (this.entity.getBedPosition().isPresent()) { this.entity.startSleeping(this.entity.getBedPosition().get()); }
    }

    @Override
    public void tick() {
        this.entity.wakeUp();
    }
}
