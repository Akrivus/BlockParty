package moe.blocks.mod.entity.goal.target;

import moe.blocks.mod.entity.FiniteEntity;
import moe.blocks.mod.entity.dating.Relationship;
import moe.blocks.mod.entity.goal.RevengeGoal;
import net.minecraft.entity.LivingEntity;

public class AvengeLeaderGoal extends RevengeGoal {

    public AvengeLeaderGoal(FiniteEntity entity) {
        super(entity);
    }

    @Override
    public boolean preCheckTarget() {
        LivingEntity leader = this.entity.getFollowTarget();
        if (leader != null) {
            Relationship relationship = this.entity.getDatingState().get(leader);
            LivingEntity victim = this.entity.getRevengeTarget();
            if (relationship.canFightAlongside() && this.entity.canAttack(victim)) {
                if (relationship.canDieFor() || this.entity.isSuperiorTo(victim)) {
                    this.victim = victim;
                    return true;
                }
            }
        }
        return false;
    }
}
