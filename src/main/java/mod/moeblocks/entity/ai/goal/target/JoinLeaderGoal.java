package mod.moeblocks.entity.ai.goal.target;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.ai.Relationship;
import mod.moeblocks.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.LivingEntity;

public class JoinLeaderGoal extends RevengeGoal {

    public JoinLeaderGoal(StateEntity entity) {
        super(entity);
    }

    @Override
    public boolean preCheckTarget() {
        LivingEntity leader = this.entity.getFollowTarget();
        if (leader != null) {
            Relationship relationship = this.entity.getRelationships().get(leader);
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
