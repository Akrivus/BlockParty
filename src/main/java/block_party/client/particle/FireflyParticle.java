package block_party.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FireflyParticle extends TextureSheetParticle {
    private final double origPosX;
    private final double origPosY;
    private final double origPosZ;
    private final double bearing;
    private final double distance;
    private final double factor;

    public FireflyParticle(SpriteSet sprite, ClientLevel level, double x, double y, double z, double bearing, double distance, double factor) {
        super(level, x, y, z, 0.1, 0.2, 0.1);
        this.setSize(0.25F, 0.25F);
        this.pickSprite(sprite);
        this.origPosX = x;
        this.origPosY = y;
        this.origPosZ = z;
        this.bearing = bearing;
        this.distance = distance;
        this.factor = factor;
        this.lifetime = 200;
    }

    @Override
    public void tick() {
        this.quadSize = (float) Math.min(0.125, Math.max(0, Math.sin(this.age / 10.0 + Math.sin(this.age / 20.0)) / 4.0));
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (++this.age > this.lifetime && this.quadSize == 0) {
            this.remove();
        } else if (this.age < 100) {
            this.yd *= this.y - this.origPosY < 1.0 ? 1.1F : 0.9F;
            this.move(this.xd, this.yd, this.zd);
        } else {
            this.xd *= 0.9F;
            this.zd *= 0.9F;
            this.move(this.xd, 0, this.zd);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xf000f0;
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
