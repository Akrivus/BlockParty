package moeblocks.mod.entity.ai.behavior;

import moeblocks.mod.entity.util.Behaviors;

public class SeaLanternBehavior extends BasicBehavior {
    @Override
    public boolean isGlowing() {
        return true;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.SEA_LANTERN;
    }
}
