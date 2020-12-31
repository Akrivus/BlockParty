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

public enum HealthState implements IStateEnum<AbstractNPCEntity> {
    PERFECT((npc, list) -> {
    
    }, (npc) -> npc.getHealth(), 16, 20),
    GOOD((npc, list) -> {
    
    }, (npc) -> npc.getHealth(), 12, 16),
    FAIR((npc, list) -> {
    
    }, (npc) -> npc.getHealth(), 8, 12),
    SERIOUS((npc, list) -> {
    
    }, (npc) -> npc.getHealth(), 4, 8),
    CRITICAL((npc, list) -> {
    
    }, (npc) -> npc.getHealth(), 0, 4);
    
    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> function;
    private final float start;
    private final float end;
    
    HealthState(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
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
        return HealthState.get(key);
    }
    
    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return HealthState.values();
    }
    
    public static HealthState get(String key) {
        try {
            return HealthState.valueOf(key);
        } catch (IllegalArgumentException e) {
            return HealthState.PERFECT;
        }
    }
}
