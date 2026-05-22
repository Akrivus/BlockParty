package block_party.client.renderers.layers;

import block_party.client.model.MoeModel;
import block_party.client.renderers.layers.special.SpecialBlockOverlay;
import block_party.entities.Moe;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.function.Supplier;

public class SpecialLayer extends RenderLayer<Moe, MoeModel<Moe>> {
    private static final HashMap<Block, Supplier<SpecialBlockOverlay>> OVERLAYS = new HashMap<>();

    public SpecialLayer(RenderLayerParent<Moe, MoeModel<Moe>> renderer) {
        super(renderer);
    }

    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, Moe moe, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Supplier<SpecialBlockOverlay> supplier = OVERLAYS.get(moe.getBlock());
        if (moe.isInvisible() || supplier == null) { return; }
        supplier.get().render(this.getParentModel(), moe, stack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    public static void registerOverlay(Block block, Supplier<SpecialBlockOverlay> overlay) {
        OVERLAYS.put(block, overlay);
    }
}