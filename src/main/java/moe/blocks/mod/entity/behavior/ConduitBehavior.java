package moe.blocks.mod.entity.behavior;

import moe.blocks.mod.entity.util.Behaviors;

public class ConduitBehavior extends BasicBehavior {
    @Override
    public float getBlockVolume() {
        return 1.0F;
    }

    @Override
    public boolean isGlowing() {
        return true;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.CONDUIT;
    }
}
