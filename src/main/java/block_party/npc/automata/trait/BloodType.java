package block_party.npc.automata.trait;

import block_party.npc.BlockPartyNPC;
import block_party.npc.automata.ITrait;
import block_party.scene.ISceneRequirement;
import net.minecraft.world.entity.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum BloodType implements ITrait<BloodType> {
    AB(1), B(3, AB), A(5, AB), O(7, A, B, AB);

    private final int weight;
    private final List<BloodType> compatibilities;

    BloodType(int weight, BloodType... compatibilities) {
        this.weight = weight;
        this.compatibilities = Arrays.asList(compatibilities);
    }

    @Override
    public boolean isSharedWith(BlockPartyNPC npc) {
        return npc.getBloodType() == this;
    }

    @Override
    public String getValue() {
        return this.name();
    }

    @Override
    public BloodType fromValue(String key) {
        try {
            return BloodType.valueOf(key.toUpperCase());
        } catch (IllegalArgumentException e) {
            return this;
        }
    }

    public boolean isCompatible(BloodType them) {
        return this == them || this.compatibilities.contains(them);
    }

    public BloodType weigh(Random rand) {
        int value = rand.nextInt(8);
        for (BloodType type : BloodType.values()) {
            if (value < type.getWeight()) { return type; }
        }
        return this;
    }

    public int getWeight() {
        return this.weight;
    }
}
