package moeblocks.automata.state.enums;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.ValueGoalState;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeTriggers;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum TimeOfDay implements IStateEnum<AbstractNPCEntity> {
    MORNING((npc, list) -> {
    
    }, (npc) -> (float) npc.world.getDayTime(), 0, 3000),
    NOON((npc, list) -> {
    
    }, (npc) -> (float) npc.world.getDayTime(), 3000, 6000),
    EVENING((npc, list) -> {
    
    }, (npc) -> (float) npc.world.getDayTime(), 6000, 9000),
    DUSK((npc, list) -> {
    
    }, (npc) -> (float) npc.world.getDayTime(), 9000, 12000),
    NIGHT((npc, list) -> {
    
    }, (npc) -> (float) npc.world.getDayTime(), 12000, 15000),
    MIDNIGHT((npc, list) -> {
    
    }, (npc) -> (float) npc.world.getDayTime(), 15000, 18000),
    MORROW((npc, list) -> {
    
    }, (npc) -> (float) npc.world.getDayTime(), 18000, 21000),
    DAWN((npc, list) -> {
    
    }, (npc) -> (float) npc.world.getDayTime(), 21000, 24000);
    
    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> function;
    private final float start;
    private final float end;
    
    TimeOfDay(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
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
        return TimeOfDay.get(key);
    }
    
    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return TimeOfDay.values();
    }
    
    public static TimeOfDay get(String key) {
        try {
            return TimeOfDay.valueOf(key);
        } catch (IllegalArgumentException e) {
            return TimeOfDay.MORNING;
        }
    }
}
