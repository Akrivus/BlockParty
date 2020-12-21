package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum StressStates implements IStateEnum<AbstractNPCEntity> {
    BROKEN((npc, list) -> {

    }, (npc) -> npc.getStress(), 16, 20),
    PANICKED((npc, list) -> {

    }, (npc) -> npc.getStress(), 12, 16),
    STRESSED((npc, list) -> {

    }, (npc) -> npc.getStress(), 8, 12),
    ALERT((npc, list) -> {

    }, (npc) -> npc.getStress(), 4, 8),
    RELAXED((npc, list) -> {

    }, (npc) -> npc.getStress(), 0, 4);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> valuator;
    private final float start;
    private final float end;

    StressStates(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> valuator, float start, float end) {
        this.generator = generator;
        this.valuator = valuator;
        this.start = start;
        this.end = end;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new GoalState.ValueBased(this, this.generator, this.valuator, this.start, this.end);
    }
}
