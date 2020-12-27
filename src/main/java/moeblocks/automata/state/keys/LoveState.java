package moeblocks.automata.state.keys;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.ValueGoalState;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum LoveState implements IStateEnum<AbstractNPCEntity> {
    INTIMATE((npc, list) -> {
    
    }, (npc) -> npc.getLove(), 16, 20),
    CLOSE((npc, list) -> {
    
    }, (npc) -> npc.getLove(), 12, 16),
    FRIENDLY((npc, list) -> {
    
    }, (npc) -> npc.getLove(), 8, 12),
    ACQUAINTED((npc, list) -> {
    
    }, (npc) -> npc.getLove(), 4, 8),
    ESTRANGED((npc, list) -> {
    
    }, (npc) -> npc.getLove(), 0, 4);
    
    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> function;
    private final float start;
    private final float end;
    
    LoveState(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
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
