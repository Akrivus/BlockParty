package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.AbstractNPCEntity;
import moe.blocks.mod.util.sort.EntityDistance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

public abstract class AbstractMoveToEntityGoal<E extends AbstractNPCEntity, T extends Entity> extends Goal implements IStateGoal {
    protected final E entity;
    protected final Class<T> type;
    protected final double speed;
    protected T target;
    protected int timeUntilReset;

    public AbstractMoveToEntityGoal(E entity, Class<T> type, double speed) {
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
        this.entity = entity;
        this.type = type;
        this.speed = speed;
    }

    @Override
    public boolean shouldExecute() {
        List<T> targets = this.entity.world.getLoadedEntitiesWithinAABB(this.type, this.entity.getBoundingBox().grow(8.0D, 2.0D, 8.0D));
        targets.sort(new EntityDistance(this.entity));
        for (T target : targets) {
            if (this.entity.canBeTarget(target) && this.entity.getDistance(target) > this.getSafeZone(target) && this.canMoveTo(target)) {
                this.target = target;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return --this.timeUntilReset > 0 && this.entity.canBeTarget(this.target);
    }

    @Override
    public void startExecuting() {
        if (this.entity.getNavigator().tryMoveToEntityLiving(this.target, this.speed)) { this.timeUntilReset = 200; }
    }

    @Override
    public void resetTask() {
        this.entity.getNavigator().clearPath();
    }

    @Override
    public void tick() {
        if (this.entity.canSee(this.target) && this.entity.getDistance(this.target) < this.getStrikeZone(this.target)) {
            this.timeUntilReset = 0;
            this.onArrival();
        }
    }

    public abstract void onArrival();

    public abstract float getStrikeZone(T target);

    public abstract float getSafeZone(T target);

    public abstract boolean canMoveTo(T target);
}