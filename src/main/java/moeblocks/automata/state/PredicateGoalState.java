package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class PredicateGoalState<O extends IStateEnum<E>, E extends AbstractNPCEntity> extends GoalState<O, E> {
    public final Predicate<E> function;

    public PredicateGoalState(O filter, BiConsumer<E, List<IStateGoal>> generator, Predicate<E> function) {
        super(filter, generator);
        this.function = function;
    }

    @Override
    public boolean canApply(E applicant) {
        return this.function.test(applicant);
    }
}
