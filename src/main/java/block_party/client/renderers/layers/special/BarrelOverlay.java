package block_party.client.renderers.layers.special;

import block_party.client.model.DollModel;
import block_party.entities.BlockPartyNPC;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BarrelOverlay extends SpecialBlockOverlay {
    public void render(DollModel model, BlockPartyNPC entity, PoseStack stack, MultiBufferSource buffer, int packedLight, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        BlockState state = entity.getVisibleBlockState().setValue(BarrelBlock.FACING, Direction.DOWN).setValue(BarrelBlock.OPEN, true);
        model.getBody().translateAndRotate(this.renderBlock(entity, state, stack, buffer, packedLight, (matrix) -> {
            matrix.scale(0.55F, 0.55F, 0.55F);
            matrix.translate(-0.5D, 1.5D, -0.5D);
        }));
    }
}