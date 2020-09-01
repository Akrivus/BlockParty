package moeblocks.mod.entity.ai.behavior;

import moeblocks.mod.entity.util.Behaviors;

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
