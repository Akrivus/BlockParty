package block_party.client.renderers.layers.special;

import block_party.client.model.MoeModel;
import block_party.client.renderers.state.MoeRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SpecialBlockOverlay {
    protected PoseStack renderBlock(MoeRenderState state, BlockState block, PoseStack stack, MultiBufferSource buffer, int packedLight, Consumer<PoseStack> consumer) {
        stack.pushPose();
        consumer.accept(stack);
        this.renderBlock(state, block, stack, buffer, packedLight);
        stack.popPose();
        return stack;
    }

    protected void renderBlock(MoeRenderState state, BlockState block, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        this.getBlockRender().renderSingleBlock(block, stack, buffer, packedLight, LivingEntityRenderer.getOverlayCoords(state, 0.0F));
    }

    protected BlockRenderDispatcher getBlockRender() {
        return Minecraft.getInstance().getBlockRenderer();
    }

    public abstract void render(MoeModel model, MoeRenderState state, PoseStack stack, MultiBufferSource buffer, int packedLight);
}
