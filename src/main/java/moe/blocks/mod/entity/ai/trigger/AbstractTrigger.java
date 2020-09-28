package moe.blocks.mod.entity.ai.trigger;

import moe.blocks.mod.entity.partial.InteractEntity;

public abstract class AbstractTrigger implements Comparable<AbstractTrigger> {
    public final int priority;

    public AbstractTrigger(int priority) {
        this.priority = priority;
    }

    public int fire(InteractEntity entity) {
        if (this.canTrigger(entity)) {
            this.trigger(entity);
            return this.getDelay(entity);
        }
        return -1;
    }

    public abstract boolean canTrigger(InteractEntity entity);

    public abstract void trigger(InteractEntity entity);

    public abstract int getDelay(InteractEntity entity);

    @Override
    public int compareTo(AbstractTrigger other) {
        return Integer.compare(this.priority, other.priority);
    }
}