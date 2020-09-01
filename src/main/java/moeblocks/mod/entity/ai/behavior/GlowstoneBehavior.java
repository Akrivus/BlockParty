package moeblocks.mod.entity.ai.behavior;

import moeblocks.mod.entity.util.Behaviors;

public class GlowstoneBehavior extends BasicBehavior {
    @Override
    public void start() {
        this.moe.setCanFly(true);
    }

    @Override
    public boolean isGlowing() {
        return true;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.GLOWSTONE;
    }
}
