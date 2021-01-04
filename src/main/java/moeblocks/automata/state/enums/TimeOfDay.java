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

    }, (npc) -> (float) npc.world.getDayTime(), 0, 3000),
    NOON((npc, goals) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 3000, 6000),
    EVENING((npc, goals) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 6000, 9000),
    DUSK((npc, goals) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 9000, 12000),
    NIGHT((npc, goals) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 12000, 15000),
    MIDNIGHT((npc, goals) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 15000, 18000),
    MORROW((npc, goals) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 18000, 21000),
    DAWN((npc, goals) -> {

    }, (npc) -> (float) npc.world.getDayTime(), 21000, 24000);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    TimeOfDay(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
        this.when(0, (npc) -> Trigger.isBetween(function.apply(npc), start, end));
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
