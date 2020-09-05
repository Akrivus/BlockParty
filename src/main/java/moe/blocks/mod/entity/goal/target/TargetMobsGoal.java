package moe.blocks.mod.entity.goal.target;

import moe.blocks.mod.entity.FiniteEntity;
import moe.blocks.mod.entity.goal.RevengeGoal;
import moe.blocks.mod.util.sort.SorterDistance;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;

import java.util.List;
import java.util.stream.Collectors;

public class TargetMobsGoal extends RevengeGoal {

    public TargetMobsGoal(FiniteEntity entity) {
        super(entity);
    }

    @Override
    public boolean preCheckTarget() {
        if (this.entity.isWaiting()) {
            List<MobEntity> victims = this.entity.world.getEntitiesWithinAABB(MobEntity.class, this.entity.getBoundingBox().grow(8.0F, 2.0F, 8.0F)).stream().filter(victim -> victim instanceof IMob && this.entity.canAttack(victim) && this.entity.isSuperiorTo(victim)).sorted(new SorterDistance(this.entity)).collect(Collectors.toList());
            this.victim = victims.isEmpty() ? null : victims.get(0);
            return true;
        }
        return false;
    }
}
