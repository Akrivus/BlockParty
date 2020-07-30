package mod.moeblocks.entity.ai.emotion;

import mod.moeblocks.entity.ai.AbstractState;
import mod.moeblocks.entity.util.Emotions;
import mod.moeblocks.register.SoundEventsMoe;
import net.minecraft.util.SoundEvent;

public abstract class AbstractEmotion extends AbstractState {
    public SoundEvent getLivingSound() {
        return SoundEventsMoe.EMOTION_NORMAL.get();
    }

    public String getPath() {
        return this.toString().toLowerCase();
    }

    @Override
    public String toString() {
        return this.getKey().name();
    }

    public Emotions getKey() {
        return Emotions.NORMAL;
    }
}
