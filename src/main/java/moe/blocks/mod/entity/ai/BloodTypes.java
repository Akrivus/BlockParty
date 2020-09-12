package moe.blocks.mod.entity.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum BloodTypes {
    AB(1), B(3, AB), A(5, AB), O(7, A, B, AB);

    private final List<BloodTypes> weights = new ArrayList<>();
    private final List<BloodTypes> compatibilities;

    BloodTypes(int weight, BloodTypes... compatibilities) {
        this.compatibilities = Arrays.asList(compatibilities);
        for (int i = 0; i < weight; ++i) {
            this.weights.add(this);
        }
    }

    public static boolean isCompatible(BloodTypes one, BloodTypes two) {
        return one == two || one.compatibilities.contains(two);
    }

    public static BloodTypes weigh(Random rand) {
        ArrayList<BloodTypes> weights = new ArrayList<>();
        for (BloodTypes bloodType : BloodTypes.values()) {
            weights.addAll(bloodType.weights);
        }
        return weights.get(rand.nextInt(weights.size()));
    }
}
