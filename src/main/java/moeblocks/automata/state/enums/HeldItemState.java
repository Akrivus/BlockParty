package moeblocks.automata.state.enums;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public enum HeldItemState implements IStateEnum<AbstractNPCEntity> {
    DEFAULT((moe, goals) -> {

    }),
    FARMER((moe, goals) -> {

    }),
    FIGHTER((moe, goals) -> {

    }),
    MINER((moe, goals) -> {

    }),
    ARCHER((moe, goals) -> {

    });

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    HeldItemState(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, Predicate<AbstractNPCEntity>... triggers) {
        for (Predicate<AbstractNPCEntity> trigger : triggers) { this.when(1, trigger); }
        this.generator = generator;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new GoalState<>(this, this.generator);
    }

    @Override
    public String toKey() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromKey(String key) {
        return HeldItemState.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return HeldItemState.values();
    }

    public static HeldItemState get(String key) {
        try { return HeldItemState.valueOf(key); } catch (IllegalArgumentException e) {
            return HeldItemState.DEFAULT;
        }
    }
}
