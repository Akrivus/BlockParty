package moeblocks.mod.entity.ai.emotion;

import moeblocks.mod.entity.util.Emotions;
import moeblocks.mod.entity.util.VoiceLines;
import net.minecraft.util.SoundEvent;

public class ScaredEmotion extends AbstractEmotion {
    @Override
    public SoundEvent getLivingSound() {
        return VoiceLines.EMOTION_SCARED.get(this.entity);
    }

    @Override
    public Emotions getKey() {
        return Emotions.SCARED;
    }
}
