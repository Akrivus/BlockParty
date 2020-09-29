package moe.blocks.mod.entity.ai.trigger;

import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.partial.InteractEntity;

import java.util.Arrays;
import java.util.function.Predicate;

public abstract class DereSpecificTrigger extends AbstractTrigger {
    private final Deres[] deres;

    public DereSpecificTrigger(int priority, Predicate<InteractEntity> function, Deres... deres) {
        super(priority, (entity) -> Arrays.stream(deres).anyMatch(dere -> entity.getDere() == dere));
        this.function.and(function);
        this.deres = deres;
    }

    public static class Emotional extends DereSpecificTrigger {
        private final Emotions emotion;
        private final int timeout;

        public Emotional(int priority, Emotions emotion, int timeout, Deres... deres) {
            super(priority, (entity) -> entity.getEmotion() == Emotions.NORMAL, deres);
            this.emotion = emotion;
            this.timeout = timeout;
        }

        public Emotional(int priority, Emotions emotion, int timeout, Predicate<InteractEntity> function, Deres... deres) {
            this(priority, emotion, timeout, deres);
            this.function.and(function);
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