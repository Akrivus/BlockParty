package block_party.client.renderers.layers;

import block_party.client.model.MoeModel;
import block_party.client.renderers.layers.special.SpecialBlockOverlay;
import block_party.client.renderers.state.MoeRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.function.Supplier;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.level.block.Block;

public class SpecialLayer extends RenderLayer<MoeRenderState, MoeModel> {
    private static final HashMap<Block, Supplier<SpecialBlockOverlay>> OVERLAYS = new HashMap<>();

    public SpecialLayer(RenderLayerParent<MoeRenderState, MoeModel> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, MoeRenderState state, float yRot, float xRot) {
        if (state.isInvisible || state.visibleBlockState == null) {
            return;
        }
        Supplier<SpecialBlockOverlay> supplier = OVERLAYS.get(state.visibleBlockState.getBlock());
        if (supplier != null) {
            supplier.get().render(this.getParentModel(), state, stack, buffer, packedLight);
        }
    }

    public static void registerOverlay(Block block, Supplier<SpecialBlockOverlay> overlay) {
        OVERLAYS.put(block, overlay);
    }
}
