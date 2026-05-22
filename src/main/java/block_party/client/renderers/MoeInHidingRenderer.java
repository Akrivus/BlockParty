package block_party.client.renderers;

import block_party.BlockParty;
import block_party.entities.MoeInHiding;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class MoeInHidingRenderer extends EntityRenderer<MoeInHiding, EntityRenderState> {
    public MoeInHidingRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

}
