package moe.blocks.mod.entity.trigger;

import moe.blocks.mod.entity.FiniteEntity;

public class AbstractTrigger implements Comparable<AbstractTrigger> {
    public final int priority;

    public AbstractTrigger(int priority) {
        this.priority = priority;
    }

    public boolean fire(FiniteEntity entity) {
        if (entity.getEmotionalTimeout() < 0 && this.canTrigger(entity)) {
            this.trigger(entity);
            return true;
        }
        return false;
    }

    public boolean canTrigger(FiniteEntity entity) {
        return this.isTriggered(entity);
    }

    public boolean isTriggered(FiniteEntity entity) {
        return false;
    }

    public void trigger(FiniteEntity entity) {

    }

    @Override
    public int compareTo(AbstractTrigger other) {
        return Integer.compare(this.priority, other.priority);
    }
}
