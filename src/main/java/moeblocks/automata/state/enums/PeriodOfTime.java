package moeblocks.automata.state.enums;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.Trigger;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum PeriodOfTime implements IStateEnum<AbstractNPCEntity> {
    ATTACHED((npc, goals) -> {

    }, (npc) -> (float) npc.getTimeSinceInteraction(), 0, 24000),
    PROTESTING((npc, goals) -> {

    }, (npc) -> (float) npc.getTimeSinceInteraction(), 24000, 72000),
    DESPAIRED((npc, goals) -> {

    }, (npc) -> (float) npc.getTimeSinceInteraction(), 72000, 240000),
    DETACHED((npc, goals) -> {

    }, (npc) -> (float) npc.getTimeSinceInteraction(), 240000, Float.MAX_VALUE);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    PeriodOfTime(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
        this.generator = generator;
        this.when(0, (npc) -> Trigger.isBetween(function.apply(npc), start, end));
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new GoalState(this, this.generator);
    }

    @Override
    public String toKey() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromKey(String key) {
        return PeriodOfTime.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return PeriodOfTime.values();
    }

    public static PeriodOfTime get(String key) {
        try {
            return PeriodOfTime.valueOf(key);
        } catch (IllegalArgumentException e) {
            return PeriodOfTime.ATTACHED;
        }
    }
}
