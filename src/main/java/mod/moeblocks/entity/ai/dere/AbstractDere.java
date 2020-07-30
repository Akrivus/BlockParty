package mod.moeblocks.entity.ai.dere;

import mod.moeblocks.entity.ai.AbstractState;
import mod.moeblocks.entity.util.Deres;

public abstract class AbstractDere extends AbstractState {
    public float[] getEyeColor() {
        return new float[]{1.0F, 1.0F, 1.0F};
    }

    public int getNameColor() {
        return 0xffffff;
    }

    @Override
    public String toString() {
        return this.getKey().name();
    }

    public Deres getKey() {
        return Deres.HIMEDERE;
    }
}
