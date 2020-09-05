package moe.blocks.mod.entity.behavior;

import moe.blocks.mod.entity.util.Behaviors;

public class BambooBehavior extends AbstractBehavior {
    @Override
    public float getBlockVolume() {
        return 1.0F;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.BAMBOO;
    }
}
