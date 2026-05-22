package block_party.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class SakuraParticle extends TextureSheetParticle {
    public SakuraParticle(SpriteSet sprite, ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(level, x, y, z, motionX, motionY, motionZ);
        this.setSize(0.25F, 0.25F);
        this.pickSprite(sprite);
        this.scale(0.5F);
        this.roll = level.random.nextFloat() * 360.0F;
        this.gravity = 0.01F;
        this.lifetime = 1000;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.onGround) {
            if (--this.lifetime < 0) {
                this.remove();
            }
        } else {
            this.oRoll = this.roll -= this.gravity;
            this.yd = Math.min(-this.gravity, this.yd - this.gravity);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.98D;
            this.yd *= 0.98D;
            this.zd *= 0.98D;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static void add(SimpleParticleType particle, Level level, BlockPos pos, RandomSource random) {
        BlockPos spawn = pos.offset(random.nextInt(), -1, random.nextInt());
        if (random.nextInt(10) == 0 && level.isEmptyBlock(spawn)) {
            double direction = level.getDayTime() / 1000.0D * 15.0D;
            double x = Math.sin(0.0174444444D * direction) * (random.nextDouble() + random.nextInt(6));
            double z = Math.cos(0.0174444444D * direction) * (random.nextDouble() + random.nextInt(6));
            double y = Math.abs(random.nextGaussian()) * -1.0D;
            level.addParticle(particle, spawn.getX(), spawn.getY() + 0.75F, spawn.getZ(), x, y, z);
        }
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Factory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ) {
            return new SakuraParticle(this.sprite, level, x, y, z, motionX, motionY, motionZ);
        }
    }
}
