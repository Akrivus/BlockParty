package moe.blocks.mod.entity.ai.behavior;

import moe.blocks.mod.entity.util.Behaviors;

public class DragonEggBehavior extends BasicBehavior {
    @Override
    public boolean isGlowing() {
        return true;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.DRAGON_EGG;
    }
}
