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

public enum HealthState implements IStateEnum<AbstractNPCEntity> {
    PERFECT((npc, goals) -> {

    }, (npc) -> npc.getHealth(), 16, 20),
    GOOD((npc, goals) -> {

    }, (npc) -> npc.getHealth(), 12, 16),
    FAIR((npc, goals) -> {

    }, (npc) -> npc.getHealth(), 8, 12),
    SERIOUS((npc, goals) -> {

    }, (npc) -> npc.getHealth(), 4, 8),
    CRITICAL((npc, goals) -> {

    }, (npc) -> npc.getHealth(), 0, 4);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    HealthState(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
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
        return HealthState.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return HealthState.values();
    }

    public static HealthState get(String key) {
        try { return HealthState.valueOf(key); } catch (IllegalArgumentException e) {
            return HealthState.PERFECT;
        }
    }
}
