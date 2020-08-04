package mod.moeblocks.entity.ai.goal.target;

import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.LivingEntity;

public class AvengeSelfGoal extends RevengeGoal {

    public AvengeSelfGoal(MoeEntity moe) {
        super(moe);
    }


    @Override
    public boolean shouldExecute() {
        LivingEntity victim = this.moe.getRevengeTarget();
        if (this.moe.canAttack(victim)) {
            if (this.moe.isSuperiorTo(victim)) {
                this.victim = victim;
                return true;
            } else {
                this.moe.setAvoidTarget(victim);
            }
        }
        return false;
    }
}
