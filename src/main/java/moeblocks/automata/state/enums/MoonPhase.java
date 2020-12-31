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

public enum MoonPhase implements IStateEnum<AbstractNPCEntity> {
    NEW((npc, list) -> {
    
    }, (npc) -> npc.world.getMoonFactor(), 0.00F, 0.25F),
    CRESCENT((npc, list) -> {
    
    }, (npc) -> npc.world.getMoonFactor(), 0.25F, 0.50F),
    QUARTER((npc, list) -> {
    
    }, (npc) -> npc.world.getMoonFactor(), 0.50F, 0.75F),
    GIBBOUS((npc, list) -> {
    
    }, (npc) -> npc.world.getMoonFactor(), 0.75F, 1.00F),
    FULL((npc, list) -> {
    
    }, (npc) -> npc.world.getMoonFactor(), 1.00F, 1.00F);
    
    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> function;
    private final float start;
    private final float end;
    
    MoonPhase(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> function, float start, float end) {
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
        return MoonPhase.get(key);
    }
    
    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return MoonPhase.values();
    }
    
    public static MoonPhase get(String key) {
        try {
            return MoonPhase.valueOf(key);
        } catch (IllegalArgumentException e) {
            return MoonPhase.FULL;
        }
    }
}
