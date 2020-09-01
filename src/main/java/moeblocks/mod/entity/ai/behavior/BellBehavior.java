package moeblocks.mod.entity.ai.behavior;

import moeblocks.mod.entity.util.Behaviors;
import moeblocks.mod.init.MoeSounds;
import net.minecraft.util.SoundEvent;

public class BellBehavior extends BasicBehavior {
    @Override
    public SoundEvent getStepSound() {
        return MoeSounds.MOE_BELL_STEP.get();
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.BELL;
    }
}
