package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public abstract class AbstractFollowEntityGoal<E extends NPCEntity, T extends Entity> extends Goal implements IStateGoal {
    protected final E entity;
    protected final Class<T> type;
    protected final double speed;
    protected T target;
    protected int timeUntilReset;

    public AbstractFollowEntityGoal(E entity, Class<T> type, double speed) {
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
        this.type = type;
        this.speed = speed;
    }

    @Override
    public boolean shouldExecute() {
        T target = this.getTarget();
        if (this.entity.canBeTarget(target) && this.entity.getDistance(target) > this.getFollowDistance(target) && this.canFollow(target)) {
            this.target = target;
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.hasPath() && this.entity.canBeTarget(this.target) && this.entity.getDistance(this.target) > this.getFollowDistance(this.target);
    }

    public abstract T getTarget();

    public abstract boolean canFollow(T target);

    @Override
    public void startExecuting() {
        if (this.entity.getNavigator().tryMoveToEntityLiving(this.target, this.speed)) {
            this.timeUntilReset = 200;
            this.onFollow();
        }
    }

    @Override
    public void resetTask() {
        this.entity.getNavigator().clearPath();
        this.target = null;
    }

    @Override
    public void tick() {
        this.entity.canSee(this.target);
        if (this.entity.getDistance(this.target) < this.getFollowDistance(this.target)) {
            this.timeUntilReset = 0;
            this.onArrival();
        }
    }

    public abstract float getFollowDistance(T target);

    public abstract void onFollow();

    public abstract void onArrival();
}