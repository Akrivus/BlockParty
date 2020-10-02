package moe.blocks.mod.entity.ai.trigger;

import moe.blocks.mod.entity.ai.automata.state.Deres;
import moe.blocks.mod.entity.ai.automata.state.Emotions;
import moe.blocks.mod.entity.AbstractNPCEntity;

import java.util.Arrays;
import java.util.function.Predicate;

public abstract class DereSpecificTrigger extends AbstractTrigger {
    private final Deres[] deres;

    public DereSpecificTrigger(int priority, Predicate<AbstractNPCEntity> function, Deres... deres) {
        super(priority, (entity) -> Arrays.stream(deres).anyMatch(dere -> entity.getDere() == dere));
        this.setAdditionalFunction(function);
        this.deres = deres;
    }

    public static class Emotional extends DereSpecificTrigger {
        private final Emotions emotion;
        private final int timeout;

        public Emotional(int priority, Emotions emotion, int timeout, Predicate<AbstractNPCEntity> function, Deres... deres) {
            this(priority, emotion, timeout, deres);
            this.setAdditionalFunction(function);
        }

        public Emotional(int priority, Emotions emotion, int timeout, Deres... deres) {
            super(priority, (entity) -> entity.getEmotion() == Emotions.NORMAL, deres);
            this.emotion = emotion;
            this.timeout = timeout;
        }

        @Override
        public void trigger(AbstractNPCEntity entity) {
            entity.setEmotion(this.emotion, this.timeout);
        }

        @Override
        public int getDelay(AbstractNPCEntity entity) {
            return this.timeout;
        }
    }
}