package mod.moeblocks.entity.ai.dere;

import mod.moeblocks.entity.util.Deres;
import net.minecraft.nbt.CompoundNBT;

public class HimeDere extends AbstractDere {
    @Override
    public void start() {

    }

    @Override
    public void tick() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void read(CompoundNBT compound) {

    }

    @Override
    public void write(CompoundNBT compound) {

    }

    @Override
    public float[] getEyeColor() {
        return new float[]{0.73F, 0.55F, 0.88F};
    }

    @Override
    public int getNameColor() {
        return 0xffffff;
    }

    @Override
    public Deres getKey() {
        return Deres.HIMEDERE;
    }
}
