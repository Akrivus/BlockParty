package moe.blocks.mod.entity.behavior;

import moe.blocks.mod.entity.util.Behaviors;

public class GlazedTerracottaBehavior extends BasicBehavior {
    @Override
    public void start() {
        this.moe.setCanFly(true);
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.GLAZED_TERRACOTTA;
    }
}
