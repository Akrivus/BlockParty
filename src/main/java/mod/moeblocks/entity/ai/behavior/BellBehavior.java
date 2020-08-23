package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;
import mod.moeblocks.init.MoeSounds;
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
