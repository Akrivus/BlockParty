package mod.moeblocks.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import mod.moeblocks.MoeMod;
import mod.moeblocks.client.model.MoeModel;
import mod.moeblocks.entity.MoeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class MoeEmotionLayer extends LayerRenderer<MoeEntity, MoeModel<MoeEntity>> {
    public MoeEmotionLayer(IEntityRenderer<MoeEntity, MoeModel<MoeEntity>> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, MoeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (this.isWithinDistance(entity.getPositionVector()) && !entity.isInvisible()) {
            this.getEntityModel().render(stack, buffer.getBuffer(RenderType.getEntityCutout(this.getEyesTexture(entity))), packedLight, LivingRenderer.getPackedOverlay(entity, 0.0F), this.getRGB(entity, 0), this.getRGB(entity, 1), this.getRGB(entity, 2), 1.0F);
            this.getEntityModel().render(stack, buffer.getBuffer(RenderType.getEntityTranslucent(this.getFaceTexture(entity))), packedLight, LivingRenderer.getPackedOverlay(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean isWithinDistance(Vec3d pos) {
        ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
        return renderInfo.getProjectedView().distanceTo(pos) < 16;
    }

    public ResourceLocation getEyesTexture(MoeEntity entity) {
        return new ResourceLocation(MoeMod.ID, String.format("textures/entity/emotion/%s.eyes.png", entity.getEmotion().getPath()));
    }

    public float getRGB(MoeEntity entity, int index) {
        return entity.getDere().getEyeColor()[index];
    }

    public ResourceLocation getFaceTexture(MoeEntity entity) {
        return new ResourceLocation(MoeMod.ID, String.format("textures/entity/emotion/%s.png", entity.getEmotion().getPath()));
    }
}