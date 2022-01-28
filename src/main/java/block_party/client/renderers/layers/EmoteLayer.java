package block_party.client.renderers.layers;

import block_party.BlockParty;
import block_party.client.model.MoeModel;
import block_party.entities.BlockPartyNPC;
import block_party.entities.Moe;
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

public class EmoteLayer extends RenderLayer<Moe, MoeModel<Moe>> {
    public EmoteLayer(RenderLayerParent<Moe, MoeModel<Moe>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, Moe moe, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (this.isWithinDistance(moe.position()) && !moe.isInvisible() && !moe.isSleeping()) {
            this.getParentModel().renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutout(this.getEyesTexture(moe))), packedLight, LivingEntityRenderer.getOverlayCoords(moe, 0.0F), this.getRGB(moe, 0), this.getRGB(moe, 1), this.getRGB(moe, 2), 1.0F);
            this.getParentModel().renderToBuffer(stack, buffer.getBuffer(RenderType.entityTranslucent(this.getFaceTexture(moe))), packedLight, LivingEntityRenderer.getOverlayCoords(moe, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean isWithinDistance(Vec3 pos) {
        Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
        return renderInfo.getPosition().distanceTo(pos) < 16;
    }

    public ResourceLocation getEyesTexture(Moe moe) {
        return BlockParty.source(String.format("textures/moe/emotions/%s.eyes.png", moe.getEmotion().toString().toLowerCase()));
    }

    public float getRGB(Moe moe, int index) {
        return moe.getEyeColor()[index];
    }

    public ResourceLocation getFaceTexture(Moe moe) {
        return BlockParty.source(String.format("textures/moe/emotions/%s.png", moe.getEmotion().toString().toLowerCase()));
    }
}