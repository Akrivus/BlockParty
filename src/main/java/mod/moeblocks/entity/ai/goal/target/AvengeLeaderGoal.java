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
    public boolean shouldExecute() {
        LivingEntity leader = this.entity.getFollowTarget();
        if (leader != null) {
            Relationship relationship = this.entity.getRelationships().get(leader);
            LivingEntity victim = leader.getRevengeTarget();
            if (relationship.canFightAlongside() && this.entity.canAttack(victim) && (this.entity.isSuperiorTo(victim) || relationship.canDieFor())) {
                this.victim = victim;
                return true;
            }
        }
        return false;
    }
}
