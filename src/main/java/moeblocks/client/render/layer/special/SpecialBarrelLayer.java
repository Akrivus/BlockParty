package moeblocks.client.render.layer.special;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.model.MoeModel;
import moeblocks.entity.MoeEntity;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class SpecialBarrelLayer extends LayerRenderer<MoeEntity, MoeModel<MoeEntity>> {
    public SpecialBarrelLayer(IEntityRenderer<MoeEntity, MoeModel<MoeEntity>> renderer) {
        super(renderer);
    }

    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, MoeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) { return; }
        if (entity.isBlock(Blocks.BARREL)) {
            BlockRendererDispatcher renderer = Minecraft.getInstance().getBlockRendererDispatcher();
            BlockState block = entity.getBlockData().with(BarrelBlock.PROPERTY_FACING, Direction.DOWN).with(BarrelBlock.PROPERTY_OPEN, true);
            stack.push();
            stack.scale(0.55F, 0.55F, 0.55F);
            stack.translate(-0.5D, 1.5D, -0.5D);
            renderer.renderBlock(block, stack, buffer, packedLight, LivingRenderer.getPackedOverlay(entity, 0.0F));
            stack.pop();
            this.getEntityModel().getBody().translateRotate(stack);
        }
    }
}