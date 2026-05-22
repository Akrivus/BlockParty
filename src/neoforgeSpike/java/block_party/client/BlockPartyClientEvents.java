package block_party.client;

import block_party.client.particle.FireflyParticle;
import block_party.client.particle.GinkgoParticle;
import block_party.client.particle.SakuraParticle;
import block_party.client.particle.WhiteSakuraParticle;
import block_party.BlockParty;
import block_party.registry.CustomParticles;
import block_party.registry.CustomResources;
import block_party.registry.resources.MoeTextureReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public final class BlockPartyClientEvents {
    private BlockPartyClientEvents() {
    }

    public static void register(IEventBus modBus) {
        modBus.addListener(BlockPartyClientEvents::registerParticleProviders);
        modBus.addListener(BlockPartyClientEvents::registerClientReloadListeners);
    }

    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(CustomParticles.FIREFLY.get(), FireflyParticle.Factory::new);
        event.registerSpriteSet(CustomParticles.GINKGO.get(), GinkgoParticle.Factory::new);
        event.registerSpriteSet(CustomParticles.SAKURA.get(), SakuraParticle.Factory::new);
        event.registerSpriteSet(CustomParticles.WHITE_SAKURA.get(), WhiteSakuraParticle.Factory::new);
    }

    private static void registerClientReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(BlockParty.source("moe_textures"), CustomResources.MOE_TEXTURES);
    }

    public static boolean hasParticleProviders() {
        return true;
    }

    public static boolean hasClientTextureReloadListener() {
        return CustomResources.MOE_TEXTURES instanceof MoeTextureReloadListener;
    }
}
