package moe.blocks.mod.entity.ai.trigger;

import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.partial.InteractEntity;

public abstract class StressTrigger extends DereSpecificTrigger {
    private final float min;
    private final float max;

    public StressTrigger(int priority, float min, float max, Deres... deres) {
        super(priority, deres);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean canTrigger(InteractEntity entity) {
        float stress = entity.getStress();
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
        public boolean canTrigger(InteractEntity entity) {
            boolean defending = !this.defensive || entity.canBeTarget(entity.getRevengeTarget()) && this.defensive;
            return super.canTrigger(entity) && defending;
        }

        @Override
        public void trigger(InteractEntity entity) {
            entity.setEmotion(this.emotion, this.timeout);
        }

        @Override
        public int getDelay(InteractEntity entity) {
            return this.timeout;
        }
    }
}