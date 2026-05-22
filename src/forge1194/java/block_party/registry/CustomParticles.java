package block_party.registry;

import block_party.BlockParty;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CustomParticles {
    public static final RegistryObject<SimpleParticleType> FIREFLY = BlockParty.PARTICLES.register("firefly", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GINKGO = BlockParty.PARTICLES.register("ginkgo", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SAKURA = BlockParty.PARTICLES.register("sakura", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> WHITE_SAKURA = BlockParty.PARTICLES.register("white_sakura", () -> new SimpleParticleType(false));

    public static void add(DeferredRegister<ParticleType<?>> registry, IEventBus bus) {
        registry.register(bus);
    }
}
