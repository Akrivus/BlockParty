package moeblocks.client.render.layer.special;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.model.MoeModel;
import moeblocks.entity.MoeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;

import java.util.function.Consumer;

public abstract class SpecialBlockOverlay {
    protected MatrixStack renderBlock(MoeEntity entity, BlockState block, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, Consumer<MatrixStack> consumer) {
        stack.push();
        consumer.accept(stack);
        this.renderBlock(entity, block, stack, buffer, packedLight);
        stack.pop();
        return stack;
    }

    protected void renderBlock(MoeEntity entity, BlockState block, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        this.getBlockRender().renderBlock(block, stack, buffer, packedLight, LivingRenderer.getPackedOverlay(entity, 0.0F));
    }

    protected BlockRendererDispatcher getBlockRender() {
        return this.getMinecraft().getBlockRendererDispatcher();
    }

    protected Minecraft getMinecraft() {
        return Minecraft.getInstance();
    }

    public abstract void render(MoeModel model, MoeEntity entity, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);
}
