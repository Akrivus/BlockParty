package mod.moeblocks.entity.ai.goal.target;

import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.entity.ai.Relationship;
import mod.moeblocks.entity.ai.goal.RevengeGoal;
import mod.moeblocks.util.DistanceCheck;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DefendLeaderGoal extends RevengeGoal {

    public DefendLeaderGoal(MoeEntity moe) {
        super(moe);
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity leader = this.moe.getFollowTarget();
        if (leader != null) {
            Relationship relationship = this.moe.getRelationships().get(leader);
            if (relationship.canDefend()) {
                List<MobEntity> victims = this.moe.world.getEntitiesWithinAABB(MobEntity.class, this.moe.getBoundingBox().grow(16.0F, 4.0F, 16.0F)).stream().filter(victim -> relationship.canFightAlongside() && leader.equals(victim.getAttackTarget()) && this.moe.canAttack(victim) && (this.moe.isSuperiorTo(victim) || relationship.canDieFor())).collect(Collectors.toList());
                victims.sort(new DistanceCheck(this.moe));
                if (!victims.isEmpty()) {
                    this.victim = victims.get(0);
                    return true;
                }
            }
        }
        return false;
    }
}
