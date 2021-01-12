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

public enum StressState implements IStateEnum<AbstractNPCEntity> {
    BURNT_OUT((npc, goals) -> {

    }, 16, 20),
    PANICKED((npc, goals) -> {

    }, 12, 16),
    ANXIOUS((npc, goals) -> {

    }, 8, 12),
    ALERT((npc, goals) -> {

    }, 4, 8),
    RELAXED((npc, goals) -> {

    }, 0, 4);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    StressState(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, float start, float end) {
        this.when(0, (npc) -> Trigger.isBetween(npc.getStress(), start, end));
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
        return StressState.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return StressState.values();
    }

    public static StressState get(String key) {
        try { return StressState.valueOf(key); } catch (IllegalArgumentException e) {
            return StressState.RELAXED;
        }
    }
}
