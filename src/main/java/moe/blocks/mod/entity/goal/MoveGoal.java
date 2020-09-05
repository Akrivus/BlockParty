package moe.blocks.mod.entity.goal;

import moe.blocks.mod.entity.FiniteEntity;
import moe.blocks.mod.util.sort.SorterDistance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;

import java.util.EnumSet;
import java.util.List;

public class MoveGoal<T extends Entity> extends Goal {
    protected final FiniteEntity entity;
    protected final Class<T> type;
    protected final double speed;
    protected T target;
    protected Path path;
    protected int timeUntilReset;

    public MoveGoal(FiniteEntity entity, Class<T> type, double speed) {
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
        this.type = type;
        this.speed = speed;
    }

    @Override
    public boolean shouldExecute() {
        List<T> targets = this.entity.world.getEntitiesWithinAABB(this.type, this.entity.getBoundingBox().grow(8.0D, 2.0D, 8.0D));
        targets.sort(new SorterDistance(this.entity));
        for (T target : targets) {
            if (this.entity.canBeTarget(target) && this.canMoveTo(target)) {
                this.path = this.entity.getNavigator().getPathToEntity(target, 0);
                this.target = target;
                return this.path != null;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return --this.timeUntilReset > 0 && this.entity.hasPath() && this.entity.canBeTarget(this.target) && this.canMoveTo(this.target);
    }

    @Override
    public void startExecuting() {
        this.entity.getNavigator().setPath(this.path, this.speed);
        this.timeUntilReset = 200;
    }

    @Override
    public void resetTask() {
        this.entity.getNavigator().clearPath();
    }

    @Override
    public void tick() {
        if (this.entity.getDistance(this.target) < this.getDistanceThreshhold()) {
            this.timeUntilReset = 0;
            this.onFollowed();
        }
    }

    public float getDistanceThreshhold() {
        return 1.0F;
    }

    public void onFollowed() {

    }

    public boolean canMoveTo(T target) {
        return this.entity.canSee(target);
    }
}