package moeblocks.automata.state.keys;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.ValueGoalState;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum PeriodOfTime implements IStateEnum<AbstractNPCEntity> {
    ATTACHED((npc, list) -> {
    
    }, (npc) -> (float) npc.getTimeSinceInteraction(), 0, 24000),
    PROTESTING((npc, list) -> {
    
    }, (npc) -> (float) npc.getTimeSinceInteraction(), 24000, 72000),
    DESPAIRED((npc, list) -> {
    
    }, (npc) -> (float) npc.getTimeSinceInteraction(), 72000, 240000),
    DETACHED((npc, list) -> {
    
    }, (npc) -> (float) npc.getTimeSinceInteraction(), 240000, Float.MAX_VALUE);
    
    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> function;
    private final float start;
    private final float end;
    
    PeriodOfTime(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
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
        return PeriodOfTime.get(key);
    }
    
    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return PeriodOfTime.values();
    }
    
    public static PeriodOfTime get(String key) {
        try {
            return PeriodOfTime.valueOf(key);
        } catch (IllegalArgumentException e) {
            return PeriodOfTime.ATTACHED;
        }
    }
}
