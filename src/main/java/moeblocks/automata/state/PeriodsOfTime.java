package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum PeriodsOfTime implements IStateEnum<AbstractNPCEntity> {
    ATTACHED((npc, list) -> {

    }, (npc) -> (float) npc.getTimeSinceLastInteraction(), 0, 24000),
    PROTESTING((npc, list) -> {

    }, (npc) -> (float) npc.getTimeSinceLastInteraction(), 24000, 72000),
    DESPAIRED((npc, list) -> {

    }, (npc) -> (float) npc.getTimeSinceLastInteraction(), 72000, 240000),
    DETACHED((npc, list) -> {

    }, (npc) -> (float) npc.getTimeSinceLastInteraction(), 240000, Float.MAX_VALUE);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> valuator;
    private final float start;
    private final float end;

    PeriodsOfTime(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> valuator, float start, float end) {
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
