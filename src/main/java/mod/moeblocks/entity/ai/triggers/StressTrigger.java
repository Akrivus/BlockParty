package mod.moeblocks.entity.ai.triggers;

import mod.moeblocks.entity.StateEntity;
import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.entity.util.Emotions;

public class StressTrigger extends DereSpecificTrigger {
    private final float min;
    private final float max;

    public StressTrigger(int priority, float min, float max, Deres... deres) {
        super(priority, deres);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean isTriggered(StateEntity entity) {
        float stress = entity.getStressStats().getStress();
        return this.min < stress && stress < this.max;
    }

    public static class Emotional extends StressTrigger {
        private final Emotions emotion;
        private final int timeout;
        private final boolean defensive;

        public Emotional(int priority, Emotions emotion, int timeout, float min, float max, boolean defensive, Deres... deres) {
            super(priority, min, max, deres);
            this.emotion = emotion;
            this.timeout = timeout;
            this.defensive = defensive;
        }

        @Override
        public boolean isTriggered(StateEntity entity) {
            boolean defending = !this.defensive || entity.canBeTarget(entity.getRevengeTarget()) && this.defensive;
            return super.isTriggered(entity) && defending;
        }

        @Override
        public void trigger(StateEntity entity) {
            entity.setEmotion(this.emotion, this.timeout);
        }
    }
}
