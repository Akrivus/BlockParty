package moeblocks.mod.entity.ai.goal.target;

import moeblocks.mod.entity.StudentEntity;
import moeblocks.mod.entity.ai.Relationship;
import moeblocks.mod.entity.ai.goal.RevengeGoal;
import moeblocks.mod.util.DistanceCheck;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DefendLeaderGoal extends RevengeGoal {

    public DefendLeaderGoal(StudentEntity entity) {
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
