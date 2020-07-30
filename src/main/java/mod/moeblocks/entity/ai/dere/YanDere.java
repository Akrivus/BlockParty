package mod.moeblocks.entity.ai.dere;

import mod.moeblocks.entity.util.Deres;
import net.minecraft.nbt.CompoundNBT;

public class YanDere extends AbstractDere {
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
        return new float[]{0.98F, 0.63F, 0.56F};
    }

    @Override
    public int getNameColor() {
        return 0xffffff;
    }

    @Override
    public Deres getKey() {
        return Deres.YANDERE;
    }
}
