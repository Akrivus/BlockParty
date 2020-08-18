package mod.moeblocks.entity.ai.emotion;

import mod.moeblocks.entity.util.Emotions;
import mod.moeblocks.entity.util.VoiceLines;
import net.minecraft.util.SoundEvent;

public class HappyEmotion extends AbstractEmotion {
    @Override
    public SoundEvent getLivingSound() {
        return VoiceLines.EMOTION_HAPPY.get(this.entity);
    }

    @Override
    public Emotions getKey() {
        return Emotions.HAPPY;
    }
}
