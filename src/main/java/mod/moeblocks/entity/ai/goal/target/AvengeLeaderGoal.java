package mod.moeblocks.entity.ai.goal.target;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.ai.Relationship;
import mod.moeblocks.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.LivingEntity;

public class AvengeLeaderGoal extends RevengeGoal {

    public AvengeLeaderGoal(StateEntity entity) {
        super(entity);
    }

    @Override
    public boolean preCheckTarget() {
        LivingEntity leader = this.entity.getFollowTarget();
        if (leader != null) {
            Relationship relationship = this.entity.getRelationships().get(leader);
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
