package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SleepGoal extends Goal {
    private final NPCEntity entity;

    public SleepGoal(NPCEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.isSleeping() && !this.entity.isTimeToSleep();
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
