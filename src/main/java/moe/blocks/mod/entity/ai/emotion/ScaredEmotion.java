package moe.blocks.mod.entity.ai.emotion;

import moe.blocks.mod.entity.util.VoiceLines;
import moe.blocks.mod.entity.util.Emotions;
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
