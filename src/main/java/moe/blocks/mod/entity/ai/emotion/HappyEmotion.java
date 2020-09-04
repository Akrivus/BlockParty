package moe.blocks.mod.entity.ai.emotion;

import moe.blocks.mod.entity.util.Emotions;
import moe.blocks.mod.entity.util.VoiceLines;
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
