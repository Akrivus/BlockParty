package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;

public class BricksBehavior extends BasicBehavior {
    @Override
    public boolean isGlowing() {
        return true;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.BRICKS;
    }
}
