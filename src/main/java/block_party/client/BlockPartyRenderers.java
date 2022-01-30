package block_party.client;

import block_party.BlockParty;
import block_party.client.model.MoeModel;
import block_party.client.renderers.MoeInHidingRenderer;
import block_party.client.renderers.MoeRenderer;
import block_party.registry.CustomEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class BlockPartyRenderers {
    public static final ModelLayerLocation MOE = new ModelLayerLocation(BlockParty.source("moe"), "moe");

    public static void register(IEventBus bus) {
        bus.addListener(BlockPartyRenderers::registerEntityRenderers);
        bus.addListener(BlockPartyRenderers::registerLayerDefinitions);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(CustomEntities.MOE.get(), MoeRenderer::new);
        e.registerEntityRenderer(CustomEntities.MOE_IN_HIDING.get(), MoeInHidingRenderer::new);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions e) {
        e.registerLayerDefinition(MOE, MoeModel::create);
    }
}
