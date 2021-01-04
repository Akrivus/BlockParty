package moeblocks.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;

import javax.annotation.Nullable;

public class SakuraParticle extends Particle implements IParticleData {

    public SakuraParticle(ClientWorld world, double x, double y, double z, double mX, double mY, double mZ) {
        super(world, x, y, z, mX, mY, mZ);
    }

    @Override
    public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {

    }

    @Override
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }

    @Override
    public ParticleType<?> getType() {
        return null;
    }

    @Override
    public void write(PacketBuffer buffer) {

    }

    @Override
    public String getParameters() {
        return null;
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {
        @Override
        public Particle makeParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double mX, double mY, double mZ) {
            return new SakuraParticle(world, x, y, z, mX, mY, mZ);
        }
    }
}
