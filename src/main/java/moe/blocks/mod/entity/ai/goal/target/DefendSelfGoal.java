package moe.blocks.mod.entity.ai.goal.target;

import moe.blocks.mod.entity.StudentEntity;
import moe.blocks.mod.entity.ai.goal.RevengeGoal;
import moe.blocks.mod.util.SorterDistance;
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
        victims.sort(new SorterDistance(this.entity));
        if (!victims.isEmpty()) {
            this.victim = victims.get(0);
            return true;
        }
        return false;
    }
}
