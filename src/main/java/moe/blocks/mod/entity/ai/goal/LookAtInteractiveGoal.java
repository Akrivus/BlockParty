package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.partial.InteractEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class LookAtInteractiveGoal extends Goal {
    private final InteractEntity entity;

    public LookAtInteractiveGoal(InteractEntity entity) {
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
