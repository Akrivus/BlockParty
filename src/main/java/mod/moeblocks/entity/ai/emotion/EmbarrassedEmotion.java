package mod.moeblocks.entity.ai.emotion;

import mod.moeblocks.entity.util.Emotions;
import mod.moeblocks.register.SoundEventsMoe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundEvent;

public class EmbarrassedEmotion extends AbstractEmotion {
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
    public SoundEvent getLivingSound() {
        return SoundEventsMoe.EMOTION_ANGRY.get();
    }

    @Override
    public Emotions getKey() {
        return Emotions.ANGRY;
    }
}
