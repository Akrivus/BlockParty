package block_party.client.render.layer;

import block_party.BlockParty;
import block_party.client.model.DollModel;
import block_party.npc.BlockPartyNPC;
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

public class MoeSleepingLayer extends RenderLayer<BlockPartyNPC, DollModel<BlockPartyNPC>> {
    public MoeSleepingLayer(RenderLayerParent<BlockPartyNPC, DollModel<BlockPartyNPC>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, BlockPartyNPC entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (this.isWithinDistance(entity.position()) && !entity.isInvisible() && entity.isSleeping()) {
            this.getParentModel().renderToBuffer(stack, buffer.getBuffer(RenderType.entityTranslucent(this.getSleepingTexture())), packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean isWithinDistance(Vec3 pos) {
        Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
        return renderInfo.getPosition().distanceTo(pos) < 16;
    }

    public ResourceLocation getSleepingTexture() {
        return BlockParty.source(String.format("textures/npc/sleeping.png"));
    }
}