package moeblocks.mod.entity.ai.triggers;

import moeblocks.mod.entity.StateEntity;

public class AbstractTrigger implements Comparable<AbstractTrigger> {
    public final int priority;

    public AbstractTrigger(int priority) {
        this.priority = priority;
    }

    public boolean fire(StateEntity entity) {
        if (entity.getEmotionalTimeout() < 0 && this.canTrigger(entity)) {
            this.trigger(entity);
            return true;
        }
        return false;
    }

    public boolean canTrigger(StateEntity entity) {
        return this.isTriggered(entity);
    }

    public boolean isTriggered(StateEntity entity) {
        return false;
    }

    public void trigger(StateEntity entity) {

    }

    @Override
    public int compareTo(AbstractTrigger other) {
        return Integer.compare(this.priority, other.priority);
    }
}
