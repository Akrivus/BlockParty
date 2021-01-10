package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.particle.SakuraParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class MoeParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MoeMod.ID);
    
    public static final RegistryObject<BasicParticleType> SAKURA = REGISTRY.register("sakura", () -> new BasicParticleType(false));

    @SubscribeEvent
    public static void registerParticleFactories(ParticleFactoryRegisterEvent e) {
        Minecraft.getInstance().particles.registerFactory(SAKURA.get(), new SakuraParticle.Factory());
    }
}
