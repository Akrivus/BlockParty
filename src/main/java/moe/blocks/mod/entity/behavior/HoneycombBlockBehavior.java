package moe.blocks.mod.entity.behavior;

import moe.blocks.mod.entity.util.Behaviors;

public class HoneycombBlockBehavior extends BasicBehavior {
    @Override
    public void start() {
        this.moe.setCanFly(true);
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.HONEYCOMB_BLOCK;
    }
}
