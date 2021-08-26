package block_party.client.render.layer;

import block_party.BlockParty;
import block_party.client.model.MoeModel;
import block_party.mob.Partyer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MoeEmotionLayer extends RenderLayer<Partyer, MoeModel<Partyer>> {
    public MoeEmotionLayer(RenderLayerParent<Partyer, MoeModel<Partyer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, Partyer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (this.isWithinDistance(entity.position()) && !entity.isInvisible() && !entity.isSleeping()) {
            this.getParentModel().renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutout(this.getEyesTexture(entity))), packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), this.getRGB(entity, 0), this.getRGB(entity, 1), this.getRGB(entity, 2), 1.0F);
            this.getParentModel().renderToBuffer(stack, buffer.getBuffer(RenderType.entityTranslucent(this.getFaceTexture(entity))), packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean isWithinDistance(Vec3 pos) {
        Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
        return renderInfo.getPosition().distanceTo(pos) < 16;
    }

    public ResourceLocation getEyesTexture(Partyer entity) {
        return new ResourceLocation(BlockParty.ID, String.format("textures/entity/partyer/emotions/%s.eyes.png", entity.getEmotion().toString().toLowerCase()));
    }

    public float getRGB(Partyer entity, int index) {
        return entity.getEyeColor()[index];
    }

    public ResourceLocation getFaceTexture(Partyer entity) {
        return new ResourceLocation(BlockParty.ID, String.format("textures/entity/partyer/emotions/%s.png", entity.getEmotion().toString().toLowerCase()));
    }
}