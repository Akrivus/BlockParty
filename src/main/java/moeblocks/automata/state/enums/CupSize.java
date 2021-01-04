package moeblocks.automata.state.enums;


import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.WatchedGoalState;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.MoeEntity;

import java.util.List;
import java.util.function.BiConsumer;

public enum CupSize implements IStateEnum<MoeEntity> {
    A((moe, goals) -> {

    }),
    B((moe, goals) -> {

    }),
    C((moe, goals) -> {

    }),
    D((moe, goals) -> {

    });

    private final BiConsumer<MoeEntity, List<AbstractStateGoal>> generator;

    CupSize(BiConsumer<MoeEntity, List<AbstractStateGoal>> generator) {
        this.when(0, (npc) -> this.equals(npc.getCupSize()));
        this.generator = generator;
    }

    @Override
    public IState getState(MoeEntity applicant) {
        return new WatchedGoalState(this, this.generator, MoeEntity.CUP_SIZE);
    }

    @Override
    public String toKey() {
        return this.name();
    }

    @Override
    public IStateEnum<MoeEntity> fromKey(String key) {
        return CupSize.get(key);
    }

    @Override
    public IStateEnum<MoeEntity>[] getKeys() {
        return CupSize.values();
    }

    public static CupSize get(String key) {
        try { return CupSize.valueOf(key); } catch (IllegalArgumentException e) {
            return CupSize.A;
        }
    }
}