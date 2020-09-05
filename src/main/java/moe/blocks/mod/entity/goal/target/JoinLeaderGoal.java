package moe.blocks.mod.entity.goal.target;

import moe.blocks.mod.entity.FiniteEntity;
import moe.blocks.mod.entity.dating.Relationship;
import moe.blocks.mod.entity.goal.RevengeGoal;
import net.minecraft.entity.LivingEntity;

public class JoinLeaderGoal extends RevengeGoal {

    public JoinLeaderGoal(FiniteEntity entity) {
        super(entity);
    }

    @Override
    public boolean preCheckTarget() {
        LivingEntity leader = this.entity.getFollowTarget();
        if (leader != null) {
            Relationship relationship = this.entity.getDatingState().get(leader);
            if (leader.ticksExisted - leader.getLastAttackedEntityTime() < 100) {
                LivingEntity victim = leader.getLastAttackedEntity();
                if (relationship.canFightAlongside() && this.entity.canAttack(victim)) {
                    if (relationship.canDieFor() || this.entity.isSuperiorTo(victim)) {
                        this.victim = victim;
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
