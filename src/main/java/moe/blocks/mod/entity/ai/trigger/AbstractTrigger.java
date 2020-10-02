package moe.blocks.mod.entity.ai.trigger;

import moe.blocks.mod.entity.AbstractNPCEntity;

import java.util.function.Predicate;

public abstract class AbstractTrigger implements Comparable<AbstractTrigger> {
    public final int priority;
    public Predicate<AbstractNPCEntity> function;

    public AbstractTrigger(int priority, Predicate<AbstractNPCEntity> function) {
        this.priority = priority;
        this.function = function;
    }

    public int fire(AbstractNPCEntity entity) {
        if (this.function.test(entity)) {
            this.trigger(entity);
            return this.getDelay(entity);
        }
        return -1;
    }

    public abstract void trigger(AbstractNPCEntity entity);

    public abstract int getDelay(AbstractNPCEntity entity);

    public void setAdditionalFunction(Predicate<AbstractNPCEntity> function) {
        this.function = this.function.and(function);
    }

    @Override
    public int compareTo(AbstractTrigger other) {
        return Integer.compare(this.priority, other.priority);
    }
}