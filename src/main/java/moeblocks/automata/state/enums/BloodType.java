package moeblocks.automata.state.enums;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.WatchedGoalState;
import moeblocks.automata.state.goal.AbstractStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

public enum BloodType implements IStateEnum<AbstractNPCEntity> {
    AB((npc, goals) -> {

    }, 1),
    B((npc, goals) -> {

    }, 3, AB),
    A((npc, goals) -> {

    }, 5, AB),
    O((npc, goals) -> {

    }, 7, A, B, AB);

    private final BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator;
    private final List<BloodType> weights = new ArrayList<>();
    private final List<BloodType> compatibilities;

    BloodType(BiConsumer<AbstractNPCEntity, List<AbstractStateGoal>> generator, int weight, BloodType... compatibilities) {
        this.when(1, (npc) -> this.equals(npc.getBloodType()));
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

    public static BloodType get(String key) {
        try { return BloodType.valueOf(key); } catch (IllegalArgumentException e) {
            return BloodType.O;
        }
    }

    public static boolean isCompatible(BloodType one, BloodType two) {
        return one == two || one.compatibilities.contains(two);
    }

    public static BloodType weigh(Random rand) {
        List<BloodType> weights = new ArrayList<>();
        for (BloodType bloodType : BloodType.values()) { weights.addAll(bloodType.weights); }
        return weights.get(rand.nextInt(weights.size()));
    }
}
