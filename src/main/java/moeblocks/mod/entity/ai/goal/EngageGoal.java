package moeblocks.mod.entity.ai.goal;

import moeblocks.mod.entity.StudentEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

public class EngageGoal<T extends LivingEntity> extends Goal {
    protected final StudentEntity entity;
    private final Class<T> type;
    protected T target;
    protected boolean engaged;
    private int timeUntilEngaging;
    private int timeUntilEngaged;

    public EngageGoal(StudentEntity entity, Class<T> type) {
        super();
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
        this.type = type;
    }

    @Override
    public boolean shouldExecute() {
        if (--this.timeUntilEngaging > 0) {
            return false;
        } else {
            List<T> entities = this.entity.world.getEntitiesWithinAABB(this.type, this.entity.getBoundingBox().grow(8.0D, 2.0D, 8.0D));
            for (T entity : entities) {
                if (this.entity.canBeTarget(entity) && this.canShareWith(entity)) {
                    this.target = entity;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.canBeTarget(this.target) && !this.engaged;
    }

    @Override
    public void startExecuting() {
        this.timeUntilEngaged = this.getEngagementTime();
        this.engaged = false;
    }

    @Override
    public void resetTask() {
        this.timeUntilEngaging = this.getEngagementInterval();
    }

    @Override
    public void tick() {
        if (this.entity.getDistance(this.target) > 2.0F) {
            this.entity.getNavigator().tryMoveToEntityLiving(this.target, this.entity.getFollowSpeed(this.target, 32.0F));
        } else if (this.entity.canSee(this.target)) {
            this.entity.getNavigator().clearPath();
            if (this.engaged) {
                this.engaged = --this.timeUntilEngaged < 0;
            } else {
                this.engage();
            }
        }
    }

    public void engage() {

    }

    public int getEngagementInterval() {
        return 200;
    }

    public int getEngagementTime() {
        return 20;
    }

    public boolean canShareWith(T entity) {
        return true;
    }
}
