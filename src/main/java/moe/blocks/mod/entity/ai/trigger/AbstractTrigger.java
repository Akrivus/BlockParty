package moe.blocks.mod.entity.ai.trigger;

import moe.blocks.mod.entity.partial.InteractEntity;

import java.util.function.Predicate;

public abstract class AbstractTrigger implements Comparable<AbstractTrigger> {
    public final int priority;
    public Predicate<InteractEntity> function;

    public AbstractTrigger(int priority, Predicate<InteractEntity> function) {
        this.priority = priority;
        this.function = function;
    }

    public int fire(InteractEntity entity) {
        if (this.function.test(entity)) {
            this.trigger(entity);
            return this.getDelay(entity);
        }
        return -1;
    }

    public abstract void trigger(InteractEntity entity);

    public abstract int getDelay(InteractEntity entity);

    public void setAdditionalFunction(Predicate<InteractEntity> function) {
        this.function = this.function.and(function);
    }

    @Override
    public int compareTo(AbstractTrigger other) {
        return Integer.compare(this.priority, other.priority);
    }
}