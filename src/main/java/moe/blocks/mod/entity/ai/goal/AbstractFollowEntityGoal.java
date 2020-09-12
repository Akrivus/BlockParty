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

    public AbstractFollowEntityGoal(E entity, Class<T> type, double speed) {
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
        this.type = type;
        this.speed = speed;
    }

    @Override
    public boolean shouldExecute() {
        T target = this.getTarget();
        if (this.entity.canBeTarget(target) && this.canFollow(target)) { this.target = target; }
        return this.target != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.hasPath() && this.entity.canBeTarget(this.target) && this.target.equals(this.getTarget()) && this.canFollow(this.target);
    }

    public abstract T getTarget();

    @Override
    public void startExecuting() {
        this.entity.getNavigator().tryMoveToEntityLiving(this.target, this.speed);
        this.entity.getLookController().setLookPositionWithEntity(this.target, this.entity.getHorizontalFaceSpeed(), this.entity.getVerticalFaceSpeed());
        this.onFollow();
    }

    public abstract boolean canFollow(T target);

    @Override
    public void resetTask() {
        this.entity.getNavigator().clearPath();
        this.target = null;
    }

    @Override
    public void tick() {
        if (this.entity.getDistance(this.target) > this.getReachDistance()) { this.startExecuting(); }
        if (this.entity.getDistance(this.target) < this.getReachDistance()) { this.onArrival(); }
    }

    public float getReachDistance() {
        return (float) (Math.pow(this.entity.getWidth() * 2.0F, 2) + this.target.getWidth());
    }

    public abstract void onFollow();

    public abstract void onArrival();
}