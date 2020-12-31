package moeblocks.automata.state.enums;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.automata.state.WatchedGoalState;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeTriggers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public enum BloodType implements IStateEnum<AbstractNPCEntity> {
    AB((moe, list) -> {
    
    }, 1),
    B((moe, list) -> {
    
    }, 3, AB),
    A((moe, list) -> {
    
    }, 5, AB),
    O((moe, list) -> {
    
    }, 7, A, B, AB);
    
    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final List<BloodType> weights = new ArrayList<>();
    private final List<BloodType> compatibilities;
    
    BloodType(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, int weight, BloodType... compatibilities) {
        this.generator = generator;
        this.compatibilities = Arrays.asList(compatibilities);
        for (int i = 0; i < weight; ++i) {
            this.weights.add(this);
        }
    }
    
    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new WatchedGoalState(this, this.generator, AbstractNPCEntity.BLOOD_TYPE);
    }
    
    @Override
    public String toKey() {
        return this.name();
    }
    
    @Override
    public IStateEnum<AbstractNPCEntity> fromKey(String key) {
        return BloodType.get(key);
    }
    
    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return BloodType.values();
    }
    
    public static boolean isCompatible(BloodType one, BloodType two) {
        return one == two || one.compatibilities.contains(two);
    }
    
    public static BloodType weigh(Random rand) {
        List<BloodType> weights = new ArrayList<>();
        for (BloodType bloodType : BloodType.values()) { weights.addAll(bloodType.weights); }
        return weights.get(rand.nextInt(weights.size()));
    }
    
    public static BloodType get(String key) {
        try {
            return BloodType.valueOf(key);
        } catch (IllegalArgumentException e) {
            return BloodType.O;
        }
    }
}
