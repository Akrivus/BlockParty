package moeblocks.entity.ai;

import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.Entity;

import java.util.EnumSet;

public abstract class AbstractFollowEntityGoal<E extends AbstractNPCEntity, T extends Entity> extends AbstractStateGoal {
    protected final E entity;
    protected final Class<T> type;
    protected final double speed;
    protected T target;
    protected int timeUntilReset;
    
    public AbstractFollowEntityGoal(E entity, Class<T> type, double speed) {
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        this.entity = entity;
        this.type = type;
        this.speed = speed;
    }
    
    @Override
    public boolean shouldExecute() {
        T target = this.getTarget();
        if (this.entity.canBeTarget(target) && this.canFollow(target)) {
            if (this.entity.getDistance(target) < this.getSafeZone(target)) { return false; }
            this.target = target;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        if (this.entity.canBeTarget(this.target) && this.canFollow(this.target)) {
            if (this.entity.getDistance(this.target) < this.getStrikeZone(this.target)) { return false; }
            return this.entity.hasPath() && --this.timeUntilReset > 0;
        }
        return false;
    }
    
    @Override
    public void startExecuting() {
        this.entity.getNavigator().tryMoveToEntityLiving(this.target, this.speed);
        this.timeUntilReset = 200;
        this.onFollow();
    }
    
    @Override
    public void resetTask() {
        this.entity.getNavigator().clearPath();
        this.target = null;
    }
    
    @Override
    public void tick() {
        this.entity.canSee(this.target);
        if (this.entity.getDistance(this.target) < this.getSafeZone(this.target)) {
            this.timeUntilReset = 0;
            this.onArrival();
        }
    }
    
    public abstract void onFollow();
    
    public abstract void onArrival();
    
    public abstract float getStrikeZone(T target);
    
    public abstract T getTarget();
    
    public abstract boolean canFollow(T target);
    
    public abstract float getSafeZone(T target);
}