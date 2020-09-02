package moe.blocks.mod.entity.ai.emotion;

import moe.blocks.mod.entity.util.VoiceLines;
import moe.blocks.mod.entity.util.Emotions;
import net.minecraft.util.SoundEvent;

public class NormalEmotion extends AbstractEmotion {
    @Override
    public SoundEvent getLivingSound() {
        return VoiceLines.EMOTION_NORMAL.get(this.entity);
    }

    @Override
    public Emotions getKey() {
        return Emotions.NORMAL;
    }
}
