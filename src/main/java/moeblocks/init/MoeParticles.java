package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.particle.FireflyParticle;
import moeblocks.particle.GinkgoParticle;
import moeblocks.particle.SakuraParticle;
import moeblocks.particle.WhiteSakuraParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MoeMod.ID);
    public static final RegistryObject<BasicParticleType> FIREFLY = REGISTRY.register("firefly", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> GINKGO = REGISTRY.register("ginkgo", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> SAKURA = REGISTRY.register("sakura", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> WHITE_SAKURA = REGISTRY.register("white_sakura", () -> new BasicParticleType(false));

    public static void registerParticleFactories(final ParticleFactoryRegisterEvent e) {
        Minecraft.getInstance().particles.registerFactory(MoeParticles.FIREFLY.get(), FireflyParticle.Factory::new);
        Minecraft.getInstance().particles.registerFactory(MoeParticles.GINKGO.get(), GinkgoParticle.Factory::new);
        Minecraft.getInstance().particles.registerFactory(MoeParticles.SAKURA.get(), SakuraParticle.Factory::new);
        Minecraft.getInstance().particles.registerFactory(MoeParticles.WHITE_SAKURA.get(), WhiteSakuraParticle.Factory::new);
    }
}
