package block_party.client.render.layer;

import block_party.client.model.MoeModel;
import block_party.client.render.layer.special.SpecialBlockOverlay;
import block_party.mob.Partyer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.function.Supplier;

public class MoeSpecialRenderer extends RenderLayer<Partyer, MoeModel<Partyer>> {
    private static final HashMap<Block, Supplier<SpecialBlockOverlay>> OVERLAYS = new HashMap<>();

    public MoeSpecialRenderer(RenderLayerParent<Partyer, MoeModel<Partyer>> renderer) {
        super(renderer);
    }

    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, Partyer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Supplier<SpecialBlockOverlay> supplier = OVERLAYS.get(entity.getExternalBlockState().getBlock());
        if (entity.isInvisible() || supplier == null) { return; }
        supplier.get().render(this.getParentModel(), entity, stack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    public static void registerOverlay(Block block, Supplier<SpecialBlockOverlay> overlay) {
        OVERLAYS.put(block, overlay);
    }
}