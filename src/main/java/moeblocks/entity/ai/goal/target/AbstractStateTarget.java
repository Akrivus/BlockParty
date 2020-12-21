package moeblocks.entity.ai.goal.target;

import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AbstractStateTarget extends Goal implements IStateGoal {
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
