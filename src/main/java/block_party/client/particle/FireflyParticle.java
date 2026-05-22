package block_party.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

public class FireflyParticle extends TextureSheetParticle {
    private final double originalY;

    public FireflyParticle(SpriteSet sprite, ClientLevel level, double x, double y, double z, double bearing, double distance, double factor) {
        super(level, x, y, z, 0.1D, 0.2D, 0.1D);
        this.setSize(0.25F, 0.25F);
        this.pickSprite(sprite);
        this.originalY = y;
        this.xd = Math.sin(bearing) * distance * factor * 0.01D;
        this.zd = Math.cos(bearing) * distance * factor * 0.01D;
        this.lifetime = 200;
    }

    @Override
    public void tick() {
        this.quadSize = (float) Math.min(0.125D, Math.max(0.0D, Math.sin(this.age / 10.0D + Math.sin(this.age / 20.0D)) / 4.0D));
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (++this.age > this.lifetime && this.quadSize == 0.0F) {
            this.remove();
        } else if (this.age < 100) {
            this.yd *= this.y - this.originalY < 1.0D ? 1.1D : 0.9D;
            this.move(this.xd, this.yd, this.zd);
        } else {
            this.xd *= 0.9D;
            this.zd *= 0.9D;
            this.move(this.xd, 0.0D, this.zd);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Factory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double bearing, double distance, double factor) {
            return new FireflyParticle(this.sprite, level, x, y, z, bearing, distance, factor);
        }
    }
}
