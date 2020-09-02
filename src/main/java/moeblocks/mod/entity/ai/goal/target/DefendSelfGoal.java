package moeblocks.mod.entity.ai.goal.target;

import moeblocks.mod.entity.StudentEntity;
import moeblocks.mod.entity.ai.goal.RevengeGoal;
import moeblocks.mod.util.DistanceCheck;
import net.minecraft.entity.MobEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DefendSelfGoal extends RevengeGoal {

    public DefendSelfGoal(StudentEntity entity) {
        super(entity);
    }

    @Override
    public boolean preCheckTarget() {
        List<MobEntity> victims = this.entity.world.getEntitiesWithinAABB(MobEntity.class, this.entity.getBoundingBox().grow(16.0F, 4.0F, 16.0F)).stream().filter(victim -> this.entity.equals(victim.getAttackTarget()) && this.entity.isSuperiorTo(victim)).collect(Collectors.toList());
        victims.sort(new DistanceCheck(this.entity));
        if (!victims.isEmpty()) {
            this.victim = victims.get(0);
            return true;
        }
        return false;
    }
}
