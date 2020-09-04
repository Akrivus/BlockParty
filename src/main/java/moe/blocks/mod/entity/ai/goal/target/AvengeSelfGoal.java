package moe.blocks.mod.entity.ai.goal.target;

import moe.blocks.mod.entity.StudentEntity;
import moe.blocks.mod.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.LivingEntity;

public class AvengeSelfGoal extends RevengeGoal {

    public AvengeSelfGoal(StudentEntity entity) {
        super(entity);
    }


    @Override
    public boolean preCheckTarget() {
        LivingEntity victim = this.entity.getRevengeTarget();
        if (this.entity.canAttack(victim)) {
            if (this.entity.isSuperiorTo(victim)) {
                this.victim = victim;
                return true;
            } else {
                this.entity.setAvoidTarget(victim);
            }
        }
        return false;
    }
}
