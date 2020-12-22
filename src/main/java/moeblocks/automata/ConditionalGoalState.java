package moeblocks.automata;

import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ConditionalGoalState<O extends IStateEnum<E>, E extends AbstractNPCEntity> extends GoalState<O, E> {
    public final Function<E, Number> valuator;
    public final float start;
    public final float end;

    public ConditionalGoalState(O filter, BiConsumer<E, List<IStateGoal>> generator, Function<E, Number> valuator, float start, float end) {
        super(filter, generator);
        this.valuator = valuator;
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean canApply(E applicant) {
        float value = (float) this.valuator.apply(applicant);
        return this.start <= value && value <= this.end;
    }
}
