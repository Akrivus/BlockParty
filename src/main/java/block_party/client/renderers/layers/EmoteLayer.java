package block_party.client.renderers.layers;

import block_party.client.model.MoeModel;
import block_party.client.renderers.state.MoeRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.util.ARGB;

public class EmoteLayer extends RenderLayer<MoeRenderState, MoeModel> {
    public EmoteLayer(RenderLayerParent<MoeRenderState, MoeModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, MoeRenderState state, float yRot, float xRot) {
        if (state.distanceToCameraSq >= 256.0D || state.isInvisible || state.eyeTexture == null || state.faceTexture == null) {
            return;
        }
        int eyeColor = ARGB.colorFromFloat(1.0F, state.eyeColor[0], state.eyeColor[1], state.eyeColor[2]);
        this.getParentModel().renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutout(state.eyeTexture)), packedLight, LivingEntityRenderer.getOverlayCoords(state, 0.0F), eyeColor);
        this.getParentModel().renderToBuffer(stack, buffer.getBuffer(RenderType.entityTranslucent(state.faceTexture)), packedLight, LivingEntityRenderer.getOverlayCoords(state, 0.0F), 0xFFFFFFFF);
    }
}
