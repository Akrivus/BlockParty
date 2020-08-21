package mod.moeblocks.entity.ai.emotion;

import mod.moeblocks.entity.util.Emotions;
import mod.moeblocks.entity.util.VoiceLines;
import net.minecraft.block.Blocks;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvent;

public class SmittenEmotion extends AbstractEmotion {
    @Override
    public void tick() {
        super.tick();
        if (this.entity.ticksExisted % 10 == 0) {
            this.entity.world.addParticle(ParticleTypes.HEART, this.entity.getCenteredRandomPosX(), this.entity.getPosY() + this.entity.getHeight(), this.entity.getPosZRandom(this.entity.getWidth() / 2.0F), this.entity.getGaussian(0.02D), this.entity.getGaussian(0.02D), this.entity.getGaussian(0.02D));
        }
    }

    @Override
    public SoundEvent getLivingSound() {
        return VoiceLines.EMOTION_SMITTEN.get(this.entity);
    }

    @Override
    public Emotions getKey() {
        return Emotions.SMITTEN;
    }
}
