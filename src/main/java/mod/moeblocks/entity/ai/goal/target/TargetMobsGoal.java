package mod.moeblocks.entity.ai.goal.target;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.ai.goal.RevengeGoal;
import mod.moeblocks.util.DistanceCheck;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;

import java.util.List;
import java.util.stream.Collectors;

public class TargetMobsGoal extends RevengeGoal {

    public TargetMobsGoal(StateEntity entity) {
        super(entity);
    }

    @Override
    public boolean preCheckTarget() {
        if (this.entity.isWaiting()) {
            List<MobEntity> victims = this.entity.world.getEntitiesWithinAABB(MobEntity.class, this.entity.getBoundingBox().grow(8.0F, 4.0F, 8.0F)).stream().filter(victim -> victim instanceof IMob && this.entity.canAttack(victim) && this.entity.isSuperiorTo(victim)).collect(Collectors.toList());
            victims.sort(new DistanceCheck(this.entity));
            this.victim = victims.isEmpty() ? null : victims.get(0);
            return true;
        }
        return false;
    }
}
