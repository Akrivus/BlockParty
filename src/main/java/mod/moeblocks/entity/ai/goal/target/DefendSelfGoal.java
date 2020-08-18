package mod.moeblocks.entity.ai.goal.target;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.ai.goal.RevengeGoal;
import mod.moeblocks.util.DistanceCheck;
import net.minecraft.entity.MobEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DefendSelfGoal extends RevengeGoal {

    public DefendSelfGoal(StateEntity entity) {
        super(entity);
    }

    @Override
    public boolean shouldExecute() {
        List<MobEntity> victims = this.entity.world.getEntitiesWithinAABB(MobEntity.class, this.entity.getBoundingBox().grow(16.0F, 4.0F, 16.0F)).stream().filter(victim -> this.entity.equals(victim.getAttackTarget()) && this.entity.canAttack(victim) && this.entity.isSuperiorTo(victim)).collect(Collectors.toList());
        victims.sort(new DistanceCheck(this.entity));
        if (!victims.isEmpty()) {
            this.victim = victims.get(0);
            return true;
        }
        return false;
    }
}
