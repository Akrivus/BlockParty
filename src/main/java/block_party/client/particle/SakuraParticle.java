package block_party.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.Random;

public class SakuraParticle extends TextureSheetParticle {
    public SakuraParticle(SpriteSet sprite, ClientLevel level, double x, double y, double z, double mX, double mY, double mZ) {
        super(level, x, y, z, mX, mY, mZ);
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
            if (--this.lifetime < 0) { this.remove(); }
        } else {
            this.oRoll = this.roll -= this.gravity;
            this.yd = Math.min(-this.gravity, this.yd - this.gravity);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.98F;
            this.yd *= 0.98F;
            this.zd *= 0.98F;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static void add(RegistryObject<SimpleParticleType> particle, Level level, BlockPos pos, Random random) {
        BlockPos spawn = pos.offset(random.nextDouble(), -1.0D, random.nextDouble());
        if (random.nextInt(10) == 0 && level.isEmptyBlock(spawn)) {
            double direction = level.getDayTime() / 1000 * 15.0D;
            double x = Math.sin(0.0174444444D * direction) * (random.nextDouble() + random.nextInt(6));
            double z = Math.cos(0.0174444444D * direction) * (random.nextDouble() + random.nextInt(6));
            double y = Math.abs(random.nextGaussian()) * -1.0D;
            double start = spawn.getY() + 0.75F;
            level.addParticle(particle.get(), spawn.getX(), start, spawn.getZ(), x, y, z);
        }
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Factory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double mX, double mY, double mZ) {
            return new SakuraParticle(this.sprite, level, x, y, z, mX, mY, mZ);
        }
    }
}
