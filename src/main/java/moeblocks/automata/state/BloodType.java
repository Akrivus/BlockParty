package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;

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
        return new GoalState(this, this.generator);
    }

    @Override
    public String toToken() {
        return this.name();
    }

    @Override
    public IStateEnum<AbstractNPCEntity> fromToken(String token) {
        if (token.isEmpty()) { return BloodType.O; }
        return BloodType.valueOf(token);
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
