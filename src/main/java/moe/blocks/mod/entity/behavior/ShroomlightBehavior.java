package moe.blocks.mod.entity.behavior;

import moe.blocks.mod.entity.util.Behaviors;

public class ShroomlightBehavior extends BasicBehavior {
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
        return Behaviors.SHROOMLIGHT;
    }
}
