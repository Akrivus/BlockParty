package moeblocks.automata.state;

import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ValueGoalState<O extends IStateEnum<E>, E extends AbstractNPCEntity> extends PredicateGoalState<O, E> {
    public ValueGoalState(O filter, BiConsumer<E, List<IStateGoal>> generator, Function<E, Float> function, float start, float end) {
        super(filter, generator, (npc) -> isBetween(function.apply(npc), start, end));
    }

    private static boolean isBetween(float value, float start, float end) {
        return start <= value && value <= end;
    }
}
