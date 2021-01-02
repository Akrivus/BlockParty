package moeblocks.automata.state.goal;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.LivingEntity;

import java.util.EnumSet;

public abstract class AbstractStateTarget extends AbstractStateGoal {
    protected final AbstractNPCEntity entity;
    protected LivingEntity target;
    
    public AbstractStateTarget(AbstractNPCEntity entity) {
        this.entity = entity;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }
    
    @Override
    public boolean shouldExecute() {
        return this.setTarget() && this.entity.canSee(this.target);
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        this.target = this.entity.getAttackTarget();
        return this.entity.canBeTarget(this.target);
    }
    
    @Override
    public void startExecuting() {
        this.entity.setAttackTarget(this.target);
    }
    
    @Override
    public void resetTask() {
        this.entity.setAttackTarget(null);
    }
    
    public abstract boolean setTarget();
}
