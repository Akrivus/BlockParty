package mod.moeblocks.entity.ai.goal.target;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.ai.Relationship;
import mod.moeblocks.entity.ai.goal.RevengeGoal;
import mod.moeblocks.util.DistanceCheck;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DefendLeaderGoal extends RevengeGoal {

    public DefendLeaderGoal(StateEntity entity) {
        super(entity);
    }

    @Override
    public boolean preCheckTarget() {
        LivingEntity leader = this.entity.getFollowTarget();
        if (leader != null) {
            Relationship relationship = this.entity.getRelationships().get(leader);
            if (relationship.canDefend()) {
                List<MobEntity> victims = this.entity.world.getEntitiesWithinAABB(MobEntity.class, this.entity.getBoundingBox().grow(16.0F, 4.0F, 16.0F)).stream().filter(victim -> leader.equals(victim.getAttackTarget()) && (this.entity.isSuperiorTo(victim) || relationship.canDieFor())).collect(Collectors.toList());
                victims.sort(new DistanceCheck(this.entity));
                if (!victims.isEmpty()) {
                    this.victim = victims.get(0);
                    return true;
                }
            }
        }
        return false;
    }
}
