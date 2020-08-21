package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;

public class BambooBehavior extends AbstractBehavior {
    @Override
    public float getBlockVolume() {
        return 0.9F;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.BAMBOO;
    }
}
