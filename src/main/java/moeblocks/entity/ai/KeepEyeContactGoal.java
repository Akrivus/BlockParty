package moeblocks.entity.ai;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class KeepEyeContactGoal extends Goal {
    private final AbstractNPCEntity entity;
    
    public KeepEyeContactGoal(AbstractNPCEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }
    
    @Override
    public boolean shouldExecute() {
        return this.entity.isInConversation() || this.entity.isBeingRummaged();
    }
    
    @Override
    public void startExecuting() {
        if (this.entity.isSleeping()) { this.entity.wakeUp(); }
        this.entity.getNavigator().clearPath();
    }
    
    @Override
    public void tick() {
        this.entity.canSee(this.entity.getProtagonist());
    }
}
