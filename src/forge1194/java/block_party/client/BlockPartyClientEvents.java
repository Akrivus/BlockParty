package block_party.client;

import block_party.client.particle.FireflyParticle;
import block_party.client.particle.GinkgoParticle;
import block_party.client.particle.SakuraParticle;
import block_party.client.particle.WhiteSakuraParticle;
import block_party.items.LetterItem;
import block_party.registry.CustomItems;
import block_party.registry.CustomParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class BlockPartyClientEvents {
    private BlockPartyClientEvents() {
    }

    public static void register(IEventBus bus) {
        bus.addListener(BlockPartyClientEvents::registerModelProperties);
        bus.addListener(BlockPartyClientEvents::registerParticleFactories);
        BlockPartyRenderers.register(bus);
    }

    private static void registerModelProperties(final FMLClientSetupEvent e) {
        ItemProperties.register(CustomItems.LETTER.get(), new ResourceLocation("closed"), (stack, world, entity, damage) -> LetterItem.isClosed(stack));
    }

    private static void registerParticleFactories(final RegisterParticleProvidersEvent e) {
        Minecraft.getInstance().particleEngine.register(CustomParticles.FIREFLY.get(), FireflyParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(CustomParticles.GINKGO.get(), GinkgoParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(CustomParticles.SAKURA.get(), SakuraParticle.Factory::new);
        Minecraft.getInstance().particleEngine.register(CustomParticles.WHITE_SAKURA.get(), WhiteSakuraParticle.Factory::new);
    }
}
