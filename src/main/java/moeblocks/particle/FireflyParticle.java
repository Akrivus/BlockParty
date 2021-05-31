package moeblocks.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FireflyParticle extends SpriteTexturedParticle {
    private final double origPosX;
    private final double origPosY;
    private final double origPosZ;

    public FireflyParticle(IAnimatedSprite sprite, ClientWorld world, double x, double y, double z) {
        super(world, x, y, z, 0.1, 0.2, 0.1);
        this.setSize(0.25F, 0.25F);
        this.selectSpriteRandomly(sprite);
        this.origPosX = x;
        this.origPosY = y;
        this.origPosZ = z;
        this.maxAge = 200;
    }

    @Override
    public void tick() {
        this.particleScale = (float) Math.min(0.125, Math.max(0, Math.sin(this.age / 10.0 + Math.sin(this.age / 20.0)) / 4.0));
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (++this.age > this.maxAge && this.particleScale == 0) {
            this.setExpired();
        } else if (this.age < 100) {
            this.motionY *= this.posY - this.origPosY < 1.0 ? 1.1F : 0.9F;
            this.move(this.motionX, this.motionY, this.motionZ);
        } else {
            this.motionX *= 0.9F;
            this.motionZ *= 0.9F;
            this.move(this.motionX, 0, this.motionZ);
        }
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 0xf000f0;
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle makeParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double mX, double mY, double mZ) {
            return new FireflyParticle(this.sprite, world, x, y, z);
        }
    }
}
