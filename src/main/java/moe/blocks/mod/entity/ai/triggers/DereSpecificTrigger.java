package moe.blocks.mod.entity.ai.triggers;

import moe.blocks.mod.entity.StudentEntity;
import moe.blocks.mod.entity.util.Deres;
import moe.blocks.mod.entity.util.Emotions;

public class DereSpecificTrigger extends AbstractTrigger {
    private final Deres[] deres;

    public DereSpecificTrigger(int priority, Deres... deres) {
        super(priority);
        this.deres = deres;
    }

    @Override
    public boolean canTrigger(StudentEntity entity) {
        return super.canTrigger(entity) && entity.getDere().matches(this.deres);
    }

    public static class Emotional extends DereSpecificTrigger {
        private final Emotions emotion;
        private final int timeout;

        public Emotional(int priority, Emotions emotion, int timeout, Deres... deres) {
            super(priority, deres);
            this.emotion = emotion;
            this.timeout = timeout;
        }

        @Override
        public void trigger(StudentEntity entity) {
            entity.setEmotion(this.emotion, this.timeout);
        }
    }
}
