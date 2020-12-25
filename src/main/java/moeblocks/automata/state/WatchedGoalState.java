package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.network.datasync.DataParameter;

import java.util.List;
import java.util.function.BiConsumer;

public class WatchedGoalState<O extends IStateEnum<E>, E extends AbstractNPCEntity> extends GoalState<O, E> {
    private final DataParameter<String> key;

    public WatchedGoalState(O filter, BiConsumer<E, List<IStateGoal>> generator, DataParameter<String> key) {
        super(filter, generator);
        this.key = key;
    }

    @Override
    public void apply(E applicant) {
        applicant.getDataManager().set(this.key, this.filter.toKey());
        super.apply(applicant);
    }
}
