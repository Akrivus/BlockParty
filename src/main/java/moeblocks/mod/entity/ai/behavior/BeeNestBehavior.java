package moeblocks.mod.entity.ai.behavior;

import moeblocks.mod.entity.util.Behaviors;

public class BeeNestBehavior extends BeehiveBehavior {
    @Override
    public void start() {
        super.start();
        this.moe.setCanFly(true);
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.BEE_NEST;
    }
}
