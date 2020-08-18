package mod.moeblocks.entity.ai.emotion;

import mod.moeblocks.entity.util.Emotions;
import mod.moeblocks.entity.util.VoiceLines;
import net.minecraft.util.SoundEvent;

public class ConfusedEmotion extends AbstractEmotion {
    @Override
    public SoundEvent getLivingSound() {
        return VoiceLines.EMOTION_CONFUSED.get(this.entity);
    }

    @Override
    public Emotions getKey() {
        return Emotions.CONFUSED;
    }
}
