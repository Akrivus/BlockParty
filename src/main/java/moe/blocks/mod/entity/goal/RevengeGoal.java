package moe.blocks.mod.entity.goal;

import moe.blocks.mod.entity.FiniteEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RevengeGoal extends TargetGoal {
    protected final FiniteEntity entity;
    protected LivingEntity victim;

    public RevengeGoal(FiniteEntity entity) {
        super(entity, true);
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
        this.entity = entity;
    }

    public boolean shouldExecute() {
        return this.preCheckTarget() && this.entity.canAttack(this.victim);
    }

    public abstract boolean preCheckTarget();

    @Override
    public void startExecuting() {
        if (this.entity.runStates(state -> state.isArmed())) {
            this.entity.setAttackTarget(this.victim);
        } else {
            List<FiniteEntity> states = this.entity.world.getEntitiesWithinAABB(FiniteEntity.class, this.entity.getBoundingBox().grow(8.0F, 4.0F, 8.0F)).stream().filter(entity -> entity.runStates(state -> state.isArmed())).collect(Collectors.toList());
            if (states.isEmpty()) {
                this.entity.setAvoidTarget(this.victim);
            } else {
                for (FiniteEntity entity : states) {
                    entity.setAttackTarget(this.victim);
                }
            }
        }
    }
}