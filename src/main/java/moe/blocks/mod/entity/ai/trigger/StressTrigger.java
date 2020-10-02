package moe.blocks.mod.entity.ai.trigger;

import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.AbstractNPCEntity;

public abstract class StressTrigger extends DereSpecificTrigger {
    public StressTrigger(int priority, float min, float max, Deres... deres) {
        super(priority, (entity) -> min < entity.getStress() && entity.getStress() < max, deres);
    }

    public static class Emotional extends StressTrigger {
        private final Emotions emotion;

        public Emotional(int priority, Emotions emotion, float min, float max, Deres... deres) {
            super(priority, min, max, deres);
            this.setAdditionalFunction((entity) -> entity.getEmotion() == Emotions.NORMAL);
            this.emotion = emotion;
        }

        @Override
        public void trigger(AbstractNPCEntity entity) {
            entity.setEmotion(this.emotion, entity.getTalkInterval());
        }

        @Override
        public int getDelay(AbstractNPCEntity entity) {
            return entity.getTalkInterval();
        }
    }
}