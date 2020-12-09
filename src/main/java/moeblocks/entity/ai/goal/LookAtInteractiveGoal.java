package moeblocks.entity.ai.goal;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LookAtInteractiveGoal extends Goal {
    private final AbstractNPCEntity entity;

    public LookAtInteractiveGoal(AbstractNPCEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.isInteracted() && this.entity.canBeTarget(this.entity.getInteractTarget());
    }

    @Override
    public void tick() {
        this.entity.canSee(this.entity.getInteractTarget());
        this.entity.getNavigator().clearPath();
    }
}
