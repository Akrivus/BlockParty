package block_party.client.render;

import block_party.client.BlockPartyRenderers;
import block_party.client.animation.Animation;
import block_party.client.model.DollModel;
import block_party.client.render.layer.MoeEmotionLayer;
import block_party.client.render.layer.MoeGlowLayer;
import block_party.client.render.layer.MoeSleepingLayer;
import block_party.client.render.layer.MoeSpecialRenderer;
import block_party.mob.BlockPartyNPC;
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

public class DollRenderer extends MobRenderer<BlockPartyNPC, DollModel<BlockPartyNPC>> {

    public DollRenderer(EntityRendererProvider.Context context) {
        super(context, new DollModel<>(context.bakeLayer(BlockPartyRenderers.DOLL)), 0.25F);
        this.addLayer(new ItemInHandLayer<>(this));
        this.addLayer(new MoeEmotionLayer(this));
        this.addLayer(new MoeSleepingLayer(this));
        this.addLayer(new MoeGlowLayer(this));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet()));
        this.addLayer(new MoeSpecialRenderer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(BlockPartyNPC entity) {
        return BlockPartyNPC.Overrides.getNameOf(entity.getExternalBlockState());
    }

    @Override
    protected void renderNameTag(BlockPartyNPC entity, Component name, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        if (entity.getAnimationKey() == Animation.YEARBOOK) { return; }
        if (Minecraft.getInstance().player.distanceTo(entity) > 8.0F) { return; }
        String[] lines = new String[] { this.getHealth(entity), name.getString(), entity.getDatabaseID().toString() };
        stack.pushPose();
        stack.translate(0.0D, entity.getBbHeight() + 0.5F, 0.0D);
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

    public String getHealth(BlockPartyNPC entity) {
        return String.format("%d / %d", (int) entity.getHealth(), (int) entity.getMaxHealth());
    }

    @Override
    public void scale(BlockPartyNPC entity, PoseStack stack, float partialTickTime) {
        super.scale(entity, stack, partialTickTime);
        entity.getAnimation().render(entity, stack, partialTickTime);
        stack.scale(0.9375F, 0.9375F, 0.9375F);
        if (entity.getAnimationKey() == Animation.YEARBOOK) { return; }
        stack.scale(entity.getScale(), entity.getScale(), entity.getScale());
        this.shadowRadius = entity.getScale() * 0.25F;
    }
}
