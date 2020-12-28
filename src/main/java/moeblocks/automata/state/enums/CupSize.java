package moeblocks.automata.state.enums;


import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.WatchedGoalState;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.MoeEntity;

import java.util.List;
import java.util.function.BiConsumer;

public enum CupSize implements IStateEnum<MoeEntity> {
    A((moe, list) -> {
    
    }, 0.0F, 3),
    B((moe, list) -> {
    
    }, 12.0F, 5),
    C((moe, list) -> {
    
    }, 24.0F, 9),
    D((moe, list) -> {
    
    }, 36.0F, 36);
    
    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final float angle;
    private final int size;
    
    CupSize(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, float angle, int size) {
        this.generator = generator;
        this.angle = angle;
        this.size = size;
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
    
    public float getAngle() {
        return this.angle;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public static CupSize get(String key) {
        try {
            return CupSize.valueOf(key);
        } catch (IllegalArgumentException e) {
            return CupSize.A;
        }
    }
}