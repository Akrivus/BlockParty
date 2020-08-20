package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.ai.AbstractState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RevengeGoal extends TargetGoal {
    protected final StateEntity entity;
    protected LivingEntity victim;

    public RevengeGoal(StateEntity entity) {
        super(entity, true);
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.entity = entity;
    }

    public abstract boolean preCheckTarget();

    public boolean shouldExecute() {
        return this.preCheckTarget() && this.entity.canAttack(this.victim);
    }

    @Override
    public void startExecuting() {
        if (this.entity.runStates(state -> state.isArmed())) {
            this.entity.setAttackTarget(this.victim);
        } else {
            List<StateEntity> states = this.entity.world.getEntitiesWithinAABB(StateEntity.class, this.entity.getBoundingBox().grow(8.0F, 4.0F, 8.0F)).stream().filter(entity -> entity.runStates(state -> state.isArmed())).collect(Collectors.toList());
            if (states.isEmpty()) {
                this.entity.setAvoidTarget(this.victim);
            } else {
                for (StateEntity entity : states) {
                    entity.setAttackTarget(this.victim);
                }
            }
        }
    }
}