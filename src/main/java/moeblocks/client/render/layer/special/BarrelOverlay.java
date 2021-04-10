package moeblocks.client.render.layer.special;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.model.MoeModel;
import moeblocks.entity.MoeEntity;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.Direction;

public class BarrelOverlay extends SpecialBlockOverlay {
    public void render(MoeModel model, MoeEntity entity, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        BlockState state = entity.getExternalBlockState().with(BarrelBlock.PROPERTY_FACING, Direction.DOWN).with(BarrelBlock.PROPERTY_OPEN, true);
        model.getBody().translateRotate(this.renderBlock(entity, state, stack, buffer, packedLight, (matrix) -> {
            matrix.scale(0.55F, 0.55F, 0.55F);
            matrix.translate(-0.5D, 1.5D, -0.5D);
        }));
    }
}