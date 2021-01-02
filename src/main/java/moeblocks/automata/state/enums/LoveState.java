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

public enum LoveState implements IStateEnum<AbstractNPCEntity> {
    INTIMATE((npc, goals) -> {

    }, (npc) -> npc.getLove(), 16, 20),
    CLOSE((npc, goals) -> {

    }, (npc) -> npc.getLove(), 12, 16),
    FRIENDLY((npc, goals) -> {

    }, (npc) -> npc.getLove(), 8, 12),
    ACQUAINTED((npc, goals) -> {

    }, (npc) -> npc.getLove(), 4, 8),
    ESTRANGED((npc, goals) -> {

    }, (npc) -> npc.getLove(), 0, 4);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    LoveState(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
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
        return LoveState.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return LoveState.values();
    }

    public static LoveState get(String key) {
        try {
            return LoveState.valueOf(key);
        } catch (IllegalArgumentException e) {
            return LoveState.ACQUAINTED;
        }
    }
}
