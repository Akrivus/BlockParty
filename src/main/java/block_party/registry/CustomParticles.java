package block_party.registry;

import block_party.BlockParty;
import block_party.client.particle.FireflyParticle;
import block_party.client.particle.GinkgoParticle;
import block_party.client.particle.SakuraParticle;
import block_party.client.particle.WhiteSakuraParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CustomParticles {
    public static final Supplier<SimpleParticleType> FIREFLY = BlockParty.PARTICLES.register("firefly", () -> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> GINKGO = BlockParty.PARTICLES.register("ginkgo", () -> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> SAKURA = BlockParty.PARTICLES.register("sakura", () -> new SimpleParticleType(false));
    public static final Supplier<SimpleParticleType> WHITE_SAKURA = BlockParty.PARTICLES.register("white_sakura", () -> new SimpleParticleType(false));

    public static void add(DeferredRegister<ParticleType<?>> registry, IEventBus bus) {
        bus.addListener(CustomParticles::registerParticleFactories);
        registry.register(bus);
    }

    private static void registerParticleFactories(final RegisterParticleProvidersEvent e) {
        Minecraft.getInstance().particleEngine.register(CustomParticles.FIREFLY.get(), FireflyParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(CustomParticles.GINKGO.get(), GinkgoParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(CustomParticles.SAKURA.get(), SakuraParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(CustomParticles.WHITE_SAKURA.get(), WhiteSakuraParticle.Factory::new);
    }
}
