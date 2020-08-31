package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;

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
