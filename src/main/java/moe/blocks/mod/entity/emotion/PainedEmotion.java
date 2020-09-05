package moe.blocks.mod.entity.emotion;

import moe.blocks.mod.entity.util.Emotions;
import moe.blocks.mod.entity.util.VoiceLines;
import net.minecraft.util.SoundEvent;

public class PainedEmotion extends AbstractEmotion {
    @Override
    public SoundEvent getLivingSound() {
        return VoiceLines.EMOTION_PAINED.get(this.entity);
    }

    @Override
    public Emotions getKey() {
        return Emotions.PAINED;
    }
}
