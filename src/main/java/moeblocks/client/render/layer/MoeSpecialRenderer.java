package moeblocks.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.model.MoeModel;
import moeblocks.client.render.layer.special.SpecialBlockOverlay;
import moeblocks.entity.MoeEntity;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

import java.util.HashMap;
import java.util.function.Supplier;

public class MoeSpecialRenderer extends LayerRenderer<MoeEntity, MoeModel<MoeEntity>> {
    private static final HashMap<Block, Supplier<SpecialBlockOverlay>> OVERLAYS = new HashMap<>();

    public MoeSpecialRenderer(IEntityRenderer<MoeEntity, MoeModel<MoeEntity>> renderer) {
        super(renderer);
    }

    public void render(MatrixStack stack, IRenderTypeBuffer buffer, int packedLight, MoeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Supplier<SpecialBlockOverlay> supplier = OVERLAYS.get(entity.getExternalBlockState().getBlock());
        if (entity.isInvisible() || supplier == null) { return; }
        supplier.get().render(this.getEntityModel(), entity, stack, buffer, packedLight, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
    }

    public static void registerOverlay(Block block, Supplier<SpecialBlockOverlay> overlay) {
        OVERLAYS.put(block, overlay);
    }
}