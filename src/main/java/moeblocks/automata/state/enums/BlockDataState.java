package moeblocks.automata.state.enums;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.BlockGoalState;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.MoeEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public enum BlockDataState implements IStateEnum<MoeEntity> {
    DEFAULT((moe, goals) -> {

    });

    private final BiConsumer<MoeEntity, List<AbstractStateGoal>> generator;

    BlockDataState(BiConsumer<MoeEntity, List<AbstractStateGoal>> generator, Predicate<MoeEntity>... triggers) {
        this.generator = generator;
        for (Predicate<MoeEntity> trigger : triggers) {
            this.when(0, trigger);
        }
    }

    @Override
    public IState getState(MoeEntity applicant) {
        return new BlockGoalState(this, this.generator);
    }

    @Override
    public String toKey() {
        return this.name();
    }

    @Override
    public IStateEnum<MoeEntity> fromKey(String key) {
        return BlockDataState.get(key);
    }

    @Override
    public IStateEnum<MoeEntity>[] getKeys() {
        return BlockDataState.values();
    }

    public static BlockDataState get(String key) {
        try { return BlockDataState.valueOf(key); } catch (IllegalArgumentException e) {
            return BlockDataState.DEFAULT;
        }
    }
}
