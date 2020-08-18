package mod.moeblocks.entity.ai.emotion;

import mod.moeblocks.entity.util.Emotions;
import mod.moeblocks.entity.util.VoiceLines;
import net.minecraft.util.SoundEvent;

public class EmbarrassedEmotion extends AbstractEmotion {
    @Override
    public SoundEvent getLivingSound() {
        return VoiceLines.EMOTION_EMBARRASSED.get(this.entity);
    }

    @Override
    public Emotions getKey() {
        return Emotions.EMBARRASSED;
    }
}
