package block_party.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class GinkgoParticle extends SakuraParticle {
    public GinkgoParticle(SpriteSet sprite, ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(sprite, level, x, y, z, motionX, motionY, motionZ);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Factory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ) {
            return new GinkgoParticle(this.sprite, level, x, y, z, motionX, motionY, motionZ);
        }
    }
}
