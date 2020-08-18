package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;

public class NetherBricksBehavior extends BasicBehavior {
    @Override
    public void start() {
        this.moe.setCanFly(true);
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.NETHER_BRICKS;
    }
}
