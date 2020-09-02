package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.StudentEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.TargetGoal;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RevengeGoal extends TargetGoal {
    protected final StudentEntity entity;
    protected LivingEntity victim;

    public RevengeGoal(StudentEntity entity) {
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
            List<StudentEntity> states = this.entity.world.getEntitiesWithinAABB(StudentEntity.class, this.entity.getBoundingBox().grow(8.0F, 4.0F, 8.0F)).stream().filter(entity -> entity.runStates(state -> state.isArmed())).collect(Collectors.toList());
            if (states.isEmpty()) {
                this.entity.setAvoidTarget(this.victim);
            } else {
                for (StudentEntity entity : states) {
                    entity.setAttackTarget(this.victim);
                }
            }
        }
    }
}