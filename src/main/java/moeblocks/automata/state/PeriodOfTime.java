package moeblocks.automata.state;

import moeblocks.automata.*;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum PeriodOfTime implements IStateEnum<AbstractNPCEntity> {
    ATTACHED((npc, list) -> {

    }, (npc) -> (float) npc.getTimeSinceInteraction(), 0, 24000),
    PROTESTING((npc, list) -> {

    }, (npc) -> (float) npc.getTimeSinceInteraction(), 24000, 72000),
    DESPAIRED((npc, list) -> {

    }, (npc) -> (float) npc.getTimeSinceInteraction(), 72000, 240000),
    DETACHED((npc, list) -> {

    }, (npc) -> (float) npc.getTimeSinceInteraction(), 240000, Float.MAX_VALUE);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> valuator;
    private final float start;
    private final float end;

    PeriodOfTime(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> valuator, float start, float end) {
        this.generator = generator;
        this.valuator = valuator;
        this.start = start;
        this.end = end;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new ConditionalGoalState(this, this.generator, this.valuator, this.start, this.end);
    }

    @Override
    public String toToken() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromToken(String token) {
        if (token.isEmpty()) { return PeriodOfTime.ATTACHED; }
        return PeriodOfTime.valueOf(token);
    }
}
