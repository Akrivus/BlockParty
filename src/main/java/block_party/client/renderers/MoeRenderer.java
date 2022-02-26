package block_party.client.renderers;

import block_party.client.BlockPartyRenderers;
import block_party.client.animation.Animation;
import block_party.client.model.MoeModel;
import block_party.client.model.SamuraiModel;
import block_party.client.renderers.layers.EmoteLayer;
import block_party.client.renderers.layers.GlowLayer;
import block_party.client.renderers.layers.SpecialLayer;
import block_party.entities.Moe;
import block_party.registry.resources.MoeTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MoeRenderer extends MobRenderer<Moe, MoeModel<Moe>> {

    public MoeRenderer(EntityRendererProvider.Context context) {
        super(context, new MoeModel<>(context.bakeLayer(BlockPartyRenderers.MOE)), 0.25F);
        this.addLayer(new ItemInHandLayer<>(this));
        this.addLayer(new EmoteLayer(this));
        this.addLayer(new GlowLayer(this));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet()));
        this.addLayer(new SpecialLayer(this));
        SamuraiModel.setArmorModels(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Moe moe) {
        return MoeTextures.get(moe);
    }

    @Override
    public void render(Moe moe, float limbSwing, float partialTickTime, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        moe.getAnimation().render(moe, stack, partialTickTime);
        super.render(moe, limbSwing, partialTickTime, stack, buffer, packedLight);
    }

    @Override
    protected void renderNameTag(Moe moe, Component name, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        if (moe.getAnimationKey() == Animation.YEARBOOK) { return; }
        if (Minecraft.getInstance().player.distanceTo(moe) > 8.0F) { return; }
        String[] lines = new String[] { this.getHealth(moe), name.getString() };
        stack.pushPose();
        stack.translate(0.0D, moe.getBbHeight() + 0.5F, 0.0D);
        stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        stack.scale(-0.025F, -0.025F, 0.025F);
        for (int i = 0; i < lines.length; ++i) {
            Font font = this.getFont();
            int x = -font.width(lines[i]) / 2;
            int y = i * -10;
            Matrix4f matrix = stack.last().pose();
            int alpha = (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
            font.drawInBatch(lines[i], x, y, 0xFFFFFFFF, false, matrix, buffer, false, alpha, packedLight);
        }
        stack.popPose();
    }

    public String getHealth(Moe moe) {
        return String.format("%d / %d", (int) moe.getHealth(), (int) moe.getMaxHealth());
    }

    @Override
    public void scale(Moe moe, PoseStack stack, float partialTickTime) {
        super.scale(moe, stack, partialTickTime);
        stack.scale(moe.getScale(), moe.getScale(), moe.getScale());
        this.shadowRadius = 0.25F * moe.getScale();
    }
}
