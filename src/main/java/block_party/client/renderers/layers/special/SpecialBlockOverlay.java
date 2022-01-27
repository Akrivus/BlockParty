package block_party.client.renderers.layers.special;

import block_party.client.model.DollModel;
import block_party.entities.BlockPartyNPC;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public abstract class SpecialBlockOverlay {
    protected PoseStack renderBlock(BlockPartyNPC entity, BlockState block, PoseStack stack, MultiBufferSource buffer, int packedLight, Consumer<PoseStack> consumer) {
        stack.pushPose();
        consumer.accept(stack);
        this.renderBlock(entity, block, stack, buffer, packedLight);
        stack.popPose();
        return stack;
    }

    protected void renderBlock(BlockPartyNPC entity, BlockState block, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        this.getBlockRender().renderSingleBlock(block, stack, buffer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F));
    }

    protected BlockRenderDispatcher getBlockRender() {
        return this.getMinecraft().getBlockRenderer();
    }

    protected Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    public abstract void render(DollModel model, BlockPartyNPC entity, PoseStack stack, MultiBufferSource buffer, int packedLight, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);
}
