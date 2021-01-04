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

public enum HungerState implements IStateEnum<AbstractNPCEntity> {
    SATISFIED((npc, goals) -> {

    }, (npc) -> npc.getFoodLevel(), 16, 20),
    HUNGRY((npc, goals) -> {

    }, (npc) -> npc.getFoodLevel(), 4, 16),
    STARVING((npc, goals) -> {

    }, (npc) -> npc.getFoodLevel(), 0, 4);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;

    HungerState(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
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
        return HungerState.get(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return HungerState.values();
    }

    public static HungerState get(String key) {
        try { return HungerState.valueOf(key); } catch (IllegalArgumentException e) {
            return HungerState.SATISFIED;
        }
    }
}
