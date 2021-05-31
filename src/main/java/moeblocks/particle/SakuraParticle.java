package moeblocks.particle;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;

import java.util.Random;

public class SakuraParticle extends SpriteTexturedParticle {
    public SakuraParticle(IAnimatedSprite sprite, ClientWorld world, double x, double y, double z, double mX, double mY, double mZ) {
        super(world, x, y, z, mX, mY, mZ);
        this.setSize(0.25F, 0.25F);
        this.selectSpriteRandomly(sprite);
        this.multiplyParticleScaleBy(0.5F);
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

    public static void add(RegistryObject<BasicParticleType> particle, World world, BlockPos pos, Random random) {
        BlockPos spawn = pos.add(random.nextDouble(), -1.0D, random.nextDouble());
        if (random.nextInt(10) == 0 && world.isAirBlock(spawn)) {
            double direction = world.getDayTime() / 1000 * 15.0D;
            double x = Math.sin(0.0174444444D * direction) * (random.nextDouble() + random.nextInt(6));
            double z = Math.cos(0.0174444444D * direction) * (random.nextDouble() + random.nextInt(6));
            double y = Math.abs(random.nextGaussian()) * -1.0D;
            double start = spawn.getY() + 0.75F;
            world.addParticle(particle.get(), spawn.getX(), start, spawn.getZ(), x, y, z);
        }
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite sprite;

        public Factory(IAnimatedSprite sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle makeParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double mX, double mY, double mZ) {
            return new SakuraParticle(this.sprite, world, x, y, z, mX, mY, mZ);
        }
    }
}
