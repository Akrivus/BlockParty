package block_party.init;

import block_party.BlockParty;
import block_party.client.particle.FireflyParticle;
import block_party.client.particle.GinkgoParticle;
import block_party.client.particle.SakuraParticle;
import block_party.client.particle.WhiteSakuraParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockPartyParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BlockParty.ID);
    public static final RegistryObject<SimpleParticleType> FIREFLY = REGISTRY.register("firefly", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GINKGO = REGISTRY.register("ginkgo", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> SAKURA = REGISTRY.register("sakura", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> WHITE_SAKURA = REGISTRY.register("white_sakura", () -> new SimpleParticleType(false));

    public static void registerParticleFactories(final ParticleFactoryRegisterEvent e) {
        Minecraft.getInstance().particleEngine.register(BlockPartyParticles.FIREFLY.get(), FireflyParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(BlockPartyParticles.GINKGO.get(), GinkgoParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(BlockPartyParticles.SAKURA.get(), SakuraParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(BlockPartyParticles.WHITE_SAKURA.get(), WhiteSakuraParticle.Factory::new);
    }
}
