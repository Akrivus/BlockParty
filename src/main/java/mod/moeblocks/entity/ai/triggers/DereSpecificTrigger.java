package mod.moeblocks.entity.ai.triggers;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.entity.util.Emotions;

public class DereSpecificTrigger extends AbstractTrigger {
    private final Deres[] deres;

    public DereSpecificTrigger(int priority, Deres... deres) {
        super(priority);
        this.deres = deres;
    }

    @Override
    public boolean canTrigger(StateEntity entity) {
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
        public void trigger(StateEntity entity) {
            entity.setEmotion(this.emotion, this.timeout);
        }
    }
}
