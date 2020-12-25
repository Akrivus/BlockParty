package moeblocks.automata.state.keys;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.ValueGoalState;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum StressState implements IStateEnum<AbstractNPCEntity> {
    BROKEN((npc, list) -> {

    }, (npc) -> npc.getStress(), 16, 20),
    PANICKED((npc, list) -> {

    }, (npc) -> npc.getStress(), 12, 16),
    STRESSED((npc, list) -> {

    }, (npc) -> npc.getStress(), 8, 12),
    ALERT((npc, list) -> {

    }, (npc) -> npc.getStress(), 4, 8),
    RELAXED((npc, list) -> {

    }, (npc) -> npc.getStress(), 0, 4);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> function;
    private final float start;
    private final float end;

    StressState(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
        this.generator = generator;
        this.function = function;
        this.start = start;
        this.end = end;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new ValueGoalState(this, this.generator, this.function, this.start, this.end);
    }

    @Override
    public String toKey() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromKey(String key) {
        if (key.isEmpty()) { return StressState.RELAXED; }
        return StressState.valueOf(key);
    }

    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return StressState.values();
    }
}
