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

    }, 0, 24000),
    PROTESTING((npc, goals) -> {

    }, 24000, 72000),
    DESPAIRED((npc, goals) -> {

    }, 72000, 240000),
    DETACHED((npc, goals) -> {

    }, 240000, Float.MAX_VALUE);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    PeriodOfTime(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, float start, float end) {
        this.when(1, (npc) -> Trigger.isBetween(npc.getTimeSinceInteraction(), start, end));
        this.generator = generator;
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
        try { return PeriodOfTime.valueOf(key); } catch (IllegalArgumentException e) {
            return PeriodOfTime.ATTACHED;
        }
    }
}
