package moe.blocks.mod.entity.ai.triggers;

import moe.blocks.mod.entity.StudentEntity;

public class AbstractTrigger implements Comparable<AbstractTrigger> {
    public final int priority;

    public AbstractTrigger(int priority) {
        this.priority = priority;
    }

    public boolean fire(StudentEntity entity) {
        if (entity.getEmotionalTimeout() < 0 && this.canTrigger(entity)) {
            this.trigger(entity);
            return true;
        }
        return false;
    }

    public boolean canTrigger(StudentEntity entity) {
        return this.isTriggered(entity);
    }

    public boolean isTriggered(StudentEntity entity) {
        return false;
    }

    public void trigger(StudentEntity entity) {

    }

    @Override
    public int compareTo(AbstractTrigger other) {
        return Integer.compare(this.priority, other.priority);
    }
}
