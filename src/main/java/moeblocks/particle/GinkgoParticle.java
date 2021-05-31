package moeblocks.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class GinkgoParticle extends SakuraParticle {

    public GinkgoParticle(IAnimatedSprite sprite, ClientWorld world, double x, double y, double z, double mX, double mY, double mZ) {
        super(sprite, world, x, y, z, mX, mY, mZ);
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle makeParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double mX, double mY, double mZ) {
            return new GinkgoParticle(this.sprite, world, x, y, z, mX, mY, mZ);
        }
    }
}
