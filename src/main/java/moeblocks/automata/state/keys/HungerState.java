package moeblocks.automata.state.keys;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.ValueGoalState;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum HungerState implements IStateEnum<AbstractNPCEntity> {
    SATISFIED((npc, list) -> {
    
    }, (npc) -> npc.getFoodLevel(), 16, 20),
    HUNGRY((npc, list) -> {
    
    }, (npc) -> npc.getFoodLevel(), 4, 16),
    STARVING((npc, list) -> {
    
    }, (npc) -> npc.getFoodLevel(), 0, 4);
    
    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> function;
    private final float start;
    private final float end;
    
    HungerState(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
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
        return HungerState.get(key);
    }
    
    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return HungerState.values();
    }
    
    public static HungerState get(String key) {
        try {
            return HungerState.valueOf(key);
        } catch (IllegalArgumentException e) {
            return HungerState.SATISFIED;
        }
    }
}
