package block_party.client.renderers.layers;

import block_party.client.model.MoeModel;
import block_party.client.renderers.state.MoeRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class GlowLayer extends RenderLayer<MoeRenderState, MoeModel> {
    public GlowLayer(RenderLayerParent<MoeRenderState, MoeModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, MoeRenderState state, float yRot, float xRot) {
        if (state.distanceToCameraSq < 256.0D && !state.isInvisible && state.hasGlow && state.glowTexture != null) {
            this.getParentModel().renderToBuffer(stack, buffer.getBuffer(RenderType.eyes(state.glowTexture)), 15728640, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        }
    }
}
