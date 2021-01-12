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

    }, 16, 20),
    GOOD((npc, goals) -> {

    }, 12, 16),
    FAIR((npc, goals) -> {

    }, 8, 12),
    SERIOUS((npc, goals) -> {

    }, 4, 8),
    CRITICAL((npc, goals) -> {

    }, 0, 4);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    HealthState(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, float start, float end) {
        this.when(0, (npc) -> Trigger.isBetween(npc.getHealth(), start, end));
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
