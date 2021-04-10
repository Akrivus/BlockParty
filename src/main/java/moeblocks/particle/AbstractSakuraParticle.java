package moeblocks.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.fml.RegistryObject;

public class AbstractSakuraParticle extends SpriteTexturedParticle {
    private final RegistryObject<BasicParticleType> particle;

    public AbstractSakuraParticle(RegistryObject<BasicParticleType> particle, IAnimatedSprite sprite, ClientWorld world, double x, double y, double z, double mX, double mY, double mZ) {
        super(world, x, y, z, mX, mY, mZ);
        this.setSize(0.25F, 0.25F);
        this.selectSpriteRandomly(sprite);
        this.multiplyParticleScaleBy(0.5F);
        this.particle = particle;
        this.particleAngle = world.rand.nextFloat() * 360.0F;
        this.particleGravity = 0.01F;
        this.maxAge = 1000;
    }

    @Override
    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.onGround) {
            if (--this.maxAge < 0) { this.setExpired(); }
        } else {
            this.prevParticleAngle = this.particleAngle -= this.particleGravity;
            this.motionY = Math.min(-this.particleGravity, this.motionY - this.particleGravity);
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.98F;
            this.motionY *= 0.98F;
            this.motionZ *= 0.98F;
        }
    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }
}
