package block_party.client.renderers;

import block_party.BlockParty;
import block_party.entities.MoeInHiding;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class MoeInHidingRenderer extends EntityRenderer<MoeInHiding> {
    public MoeInHidingRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(MoeInHiding moe) {
        return BlockParty.source("textures/moe/ghost.png");
    }
}
