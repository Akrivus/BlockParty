package block_party.client.renderers.layers.special;

import block_party.client.model.MoeModel;
import block_party.client.renderers.state.MoeRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BarrelOverlay extends SpecialBlockOverlay {
    @Override
    public void render(MoeModel model, MoeRenderState state, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        BlockState block = state.visibleBlockState.setValue(BarrelBlock.FACING, Direction.DOWN).setValue(BarrelBlock.OPEN, true);
        model.getBody().translateAndRotate(this.renderBlock(state, block, stack, buffer, packedLight, matrix -> {
            matrix.scale(0.55F, 0.55F, 0.55F);
            matrix.translate(-0.5D, 1.5D, -0.5D);
        }));
    }
}
