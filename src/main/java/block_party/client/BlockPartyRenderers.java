package block_party.client;

import block_party.BlockParty;
import block_party.client.model.DollModel;
import block_party.client.render.DollRenderer;
import block_party.init.BlockPartyEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BlockParty.ID, value = Dist.CLIENT)
public class BlockPartyRenderers {
    public static final ModelLayerLocation DOLL = new ModelLayerLocation(BlockParty.source("doll"), "doll");

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(BlockPartyEntities.NPC.get(), DollRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions e) {
        e.registerLayerDefinition(DOLL, DollModel::create);
    }
}
