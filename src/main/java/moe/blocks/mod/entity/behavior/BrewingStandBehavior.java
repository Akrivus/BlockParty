package moe.blocks.mod.entity.behavior;

import moe.blocks.mod.entity.util.Behaviors;

public class BrewingStandBehavior extends BasicBehavior {
    @Override
    public boolean isGlowing() {
        return true;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.BREWING_STAND;
    }
}
