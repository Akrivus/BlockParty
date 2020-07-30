package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;

public class RedstoneLampBehavior extends BasicBehavior {
    @Override
    public void start() {
        this.moe.setCanFly(true);
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.REDSTONE_LAMP;
    }

    @Override
    public boolean isGlowing() {
        return true;
    }
}
