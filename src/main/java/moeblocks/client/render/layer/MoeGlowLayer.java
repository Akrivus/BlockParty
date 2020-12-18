package moeblocks.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.model.MoeModel;
import moeblocks.entity.MoeEntity;
import moeblocks.init.MoeBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

public class MoeGlowLayer extends LayerRenderer<MoeEntity, MoeModel<MoeEntity>> {
    private final MoeModel<MoeEntity> model = new MoeModel<>();

    public MoeGlowLayer(IEntityRenderer<MoeEntity, MoeModel<MoeEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, MoeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (this.isWithinDistance(entity.getPositionVec()) && !entity.isInvisible() && entity.isBlockGlowing()) {
            this.getEntityModel().render(stack, buffer.getBuffer(RenderType.getEyes(this.getTexture(entity))), 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean isWithinDistance(Vector3d pos) {
        ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        return renderInfo.getProjectedView().distanceTo(pos) < 16;
    }

    public ResourceLocation getTexture(MoeEntity entity) {
        return MoeBlocks.getNameOf(entity.getBlockData(), "glow");
    }
}