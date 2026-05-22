package block_party.registry;

import block_party.BlockParty;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, BlockParty.ID);
    public static final Map<String, DeferredHolder<ParticleType<?>, SimpleParticleType>> ENTRIES = new LinkedHashMap<>();

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIREFLY = register("firefly");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GINKGO = register("ginkgo");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SAKURA = register("sakura");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> WHITE_SAKURA = register("white_sakura");

    private CustomParticles() {
    }

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String id) {
        DeferredHolder<ParticleType<?>, SimpleParticleType> particle = PARTICLES.register(id, () -> new SimpleParticleType(false));
        ENTRIES.put(id, particle);
        return particle;
    }

    public static void register(IEventBus modBus) {
        PARTICLES.register(modBus);
    }
}
