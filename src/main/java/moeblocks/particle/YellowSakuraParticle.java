package moeblocks.particle;

import moeblocks.init.MoeParticles;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class YellowSakuraParticle extends AbstractSakuraParticle {

    public YellowSakuraParticle(IAnimatedSprite sprite, ClientWorld world, double x, double y, double z, double mX, double mY, double mZ) {
        super(MoeParticles.YELLOW_SAKURA_PETAL, sprite, world, x, y, z, mX, mY, mZ);
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle makeParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double mX, double mY, double mZ) {
            return new YellowSakuraParticle(this.sprite, world, x, y, z, mX, mY, mZ);
        }
    }
}
