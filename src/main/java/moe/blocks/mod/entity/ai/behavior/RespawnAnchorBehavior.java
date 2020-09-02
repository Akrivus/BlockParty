package moe.blocks.mod.entity.ai.behavior;

import moe.blocks.mod.entity.util.Behaviors;

public class RespawnAnchorBehavior extends BasicBehavior {
    @Override
    public boolean isGlowing() {
        return true;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.RESPAWN_ANCHOR;
    }
}
