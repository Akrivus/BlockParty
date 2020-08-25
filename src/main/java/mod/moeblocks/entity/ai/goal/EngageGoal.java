package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.util.Emotions;
import mod.moeblocks.util.DistanceCheck;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

public class EngageGoal<T extends LivingEntity> extends Goal {
    protected final StateEntity entity;
    protected T target;
    private final Class<T> type;
    private int timeUntilEngaging;
    private int timeUntilEngaged;
    private int timeUntilReset;
    private boolean engaged;

    public EngageGoal(StateEntity entity, Class<T> type) {
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
                    this.timeUntilEngaged = this.getEngagementTime();
                    this.target = entity;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.canBeTarget(this.target) && (!this.engaged || --this.timeUntilReset < 0);
    }

    @Override
    public void startExecuting() {
        this.timeUntilReset = this.getEngagementTime();
        this.engaged = false;
    }

    @Override
    public void resetTask() {
        this.timeUntilEngaging = this.getEngagementInterval();
    }

    @Override
    public void tick() {
        if (this.entity.canSee(this.target) && this.entity.getDistance(this.target) > 8.0F) {
            this.entity.getNavigator().tryMoveToEntityLiving(this.target, this.entity.getFollowSpeed(this.target, 32.0F));
            --this.timeUntilEngaged;
            this.engaged = this.timeUntilEngaged < 0;
        } else if (this.engaged) {
            this.entity.getNavigator().clearPath();
            this.engage();
            --this.timeUntilReset;
        }
    }

    public boolean canShareWith(T entity) {
        return true;
    }

    public void engage() {

    }

    public int getEngagementInterval() {
        return 200;
    }

    public int getEngagementTime() {
        return 20;
    }

    public int getResetTime() {
        return 20;
    }
}
