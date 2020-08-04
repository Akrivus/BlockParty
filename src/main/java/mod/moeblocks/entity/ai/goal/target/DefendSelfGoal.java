package mod.moeblocks.entity.ai.goal.target;

import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.entity.ai.goal.RevengeGoal;
import mod.moeblocks.util.DistanceCheck;
import net.minecraft.entity.MobEntity;

import java.util.List;
import java.util.stream.Collectors;

public class DefendSelfGoal extends RevengeGoal {

    public DefendSelfGoal(MoeEntity moe) {
        super(moe);
    }

    @Override
    public boolean shouldExecute() {
        List<MobEntity> victims = this.moe.world.getEntitiesWithinAABB(MobEntity.class, this.moe.getBoundingBox().grow(16.0F, 4.0F, 16.0F)).stream().filter(victim -> this.moe.equals(victim.getAttackTarget()) && this.moe.canAttack(victim) && this.moe.isSuperiorTo(victim)).collect(Collectors.toList());
        victims.sort(new DistanceCheck(this.moe));
        if (!victims.isEmpty()) {
            this.victim = victims.get(0);
            return true;
        }
        return false;
    }
}
