package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;

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
