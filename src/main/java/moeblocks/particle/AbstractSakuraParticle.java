package moeblocks.particle;

import moeblocks.MoeMod;
import moeblocks.init.MoeBlocks;
import moeblocks.init.MoeParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
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
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
}
