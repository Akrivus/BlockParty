package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.util.DistanceCheck;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

public class LookGoal extends Goal {
    private final StateEntity entity;
    private LivingEntity target;

    public LookGoal(StateEntity entity) {
        super();
        this.setMutexFlags(EnumSet.of(Flag.LOOK));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.isWaiting()) {
            List<LivingEntity> victims = this.entity.world.getEntitiesWithinAABB(LivingEntity.class, this.entity.getBoundingBox().grow(8.0F, 4.0F, 8.0F));
            return !victims.isEmpty();
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.canBeTarget(this.target);
    }

    @Override
    public void startExecuting() {
        List<LivingEntity> victims = this.entity.world.getEntitiesWithinAABB(LivingEntity.class, this.entity.getBoundingBox().grow(8.0F, 4.0F, 8.0F));
        victims.sort(new DistanceCheck(this.entity));
        this.target = victims.isEmpty() ? null : victims.get(0);
    }

    @Override
    public void tick() {
        this.entity.turnToView(this.target);
    }
}