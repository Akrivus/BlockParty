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

public enum TimeOfDay implements IStateEnum<AbstractNPCEntity> {
    MORNING((npc, goals) -> {

    }, 0, 4000),
    NOON((npc, goals) -> {

    }, 4000, 8000),
    EVENING((npc, goals) -> {

    }, 8000, 12000),
    NIGHT((npc, goals) -> {

    }, 12000, 16000),
    MIDNIGHT((npc, goals) -> {

    }, 16000, 20000),
    DAWN((npc, goals) -> {

    }, 20000, 24000);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    TimeOfDay(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, float start, float end) {
        this.when(1, (npc) -> Trigger.isBetween(npc.world.getDayTime(), start, end));
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
        return TimeOfDay.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return TimeOfDay.values();
    }

    public static TimeOfDay get(String key) {
        try { return TimeOfDay.valueOf(key); } catch (IllegalArgumentException e) {
            return TimeOfDay.MORNING;
        }
    }
}
