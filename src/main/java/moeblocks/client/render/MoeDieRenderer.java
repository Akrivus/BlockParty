package moeblocks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moeblocks.MoeMod;
import moeblocks.client.model.DieCubeModel;
import moeblocks.entity.MoeDieEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class MoeDieRenderer extends EntityRenderer<MoeDieEntity> implements IRenderFactory<MoeDieEntity> {
    private static ResourceLocation DIE_TEXTURE = new ResourceLocation(MoeMod.ID, "textures/entity/moe_die.png");
    private final DieCubeModel<MoeDieEntity> model = new DieCubeModel<>();

    public MoeDieRenderer(EntityRendererManager manager) {
        super(manager);
        this.shadowSize = 0.1F;
    }

    @Override
    public EntityRenderer<MoeDieEntity> createRenderFor(EntityRendererManager manager) {
        return new MoeDieRenderer(manager);
    }

    @Override
    public void render(MoeDieEntity die, float yaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        stack.push();
        stack.scale(0.375F, 0.375F, 0.375F);
        this.model.setRotationAngles(die, partialTicks, 0.0F, -0.1F, yaw, die.rotationPitch);
        IVertexBuilder builder = buffer.getBuffer(this.model.getRenderType(this.getEntityTexture(die)));
        stack.translate(0.0F, -0.125F, 0.0F);
        this.model.render(stack, builder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        stack.pop();
        super.render(die, yaw, partialTicks, stack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getEntityTexture(MoeDieEntity entity) {
        return DIE_TEXTURE;
    }
}
