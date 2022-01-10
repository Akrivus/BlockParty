package block_party.client;

import block_party.BlockParty;
import block_party.client.model.DollModel;
import block_party.client.renderers.DollRenderer;
import block_party.registry.CustomEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class BlockPartyRenderers {
    public static final ModelLayerLocation DOLL = new ModelLayerLocation(BlockParty.source("doll"), "doll");

    public static void register(IEventBus bus) {
        bus.addListener(BlockPartyRenderers::registerEntityRenderers);
        bus.addListener(BlockPartyRenderers::registerLayerDefinitions);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(CustomEntities.NPC.get(), DollRenderer::new);
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions e) {
        e.registerLayerDefinition(DOLL, DollModel::create);
    }
}
