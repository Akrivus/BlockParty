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
    BROKEN((npc, goals) -> {

    }, (npc) -> npc.getStress(), 16, 20),
    PANICKED((npc, goals) -> {

    }, (npc) -> npc.getStress(), 12, 16),
    STRESSED((npc, goals) -> {

    }, (npc) -> npc.getStress(), 8, 12),
    ALERT((npc, goals) -> {

    }, (npc) -> npc.getStress(), 4, 8),
    RELAXED((npc, goals) -> {

    }, (npc) -> npc.getStress(), 0, 4);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    StressState(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
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
        return StressState.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return StressState.values();
    }

    public static StressState get(String key) {
        try {
            return StressState.valueOf(key);
        } catch (IllegalArgumentException e) {
            return StressState.RELAXED;
        }
    }
}
