package moe.blocks.mod.entity.ai.trigger;

import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.partial.InteractEntity;

import java.util.Arrays;

public abstract class DereSpecificTrigger extends AbstractTrigger {
    private final Deres[] deres;

    public DereSpecificTrigger(int priority, Deres... deres) {
        super(priority);
        this.deres = deres;
    }

    @Override
    public boolean canTrigger(InteractEntity entity) {
        return Arrays.stream(this.deres).anyMatch(dere -> entity.getDere() == dere);
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
        public void trigger(InteractEntity entity) {
            entity.setEmotion(this.emotion, this.timeout);
        }

        @Override
        public int getDelay(InteractEntity entity) {
            return this.timeout;
        }
    }
}