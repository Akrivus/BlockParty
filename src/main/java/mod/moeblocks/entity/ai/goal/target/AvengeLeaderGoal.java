package mod.moeblocks.entity.ai.goal.target;

import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.entity.ai.Relationship;
import mod.moeblocks.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.LivingEntity;

public class AvengeLeaderGoal extends RevengeGoal {

    public AvengeLeaderGoal(MoeEntity moe) {
        super(moe);
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity leader = this.moe.getFollowTarget();
        if (leader != null) {
            Relationship relationship = this.moe.getRelationships().get(leader);
            LivingEntity victim = leader.getRevengeTarget();
            if (relationship.canFightAlongside() && this.moe.canAttack(victim) && (this.moe.isSuperiorTo(victim) || relationship.canDieFor())) {
                this.victim = victim;
                return true;
            }
        }
        return false;
    }
}
