package moeblocks.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.animation.Animation;
import moeblocks.client.model.MoeModel;
import moeblocks.client.render.layer.MoeEmotionLayer;
import moeblocks.client.render.layer.MoeGlowLayer;
import moeblocks.client.render.layer.MoeSleepingLayer;
import moeblocks.client.render.layer.MoeSpecialRenderer;
import moeblocks.entity.MoeEntity;
import moeblocks.init.MoeOverrides;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class MoeRenderer extends MobRenderer<MoeEntity, MoeModel<MoeEntity>> implements IRenderFactory<MoeEntity> {

    public MoeRenderer(EntityRendererManager manager) {
        super(manager, new MoeModel<>(), 0.25F);
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new MoeEmotionLayer(this));
        this.addLayer(new MoeSleepingLayer(this));
        this.addLayer(new MoeGlowLayer(this));
        this.addLayer(new HeadLayer<>(this));
        this.addLayer(new MoeSpecialRenderer(this));
    }

    @Override
    public MobRenderer<MoeEntity, MoeModel<MoeEntity>> createRenderFor(EntityRendererManager manager) {
        return new MoeRenderer(manager);
    }

    @Override
    public ResourceLocation getEntityTexture(MoeEntity entity) {
        return MoeOverrides.getNameOf(entity.getExternalBlockState());
    }

    @Override
    protected void renderName(MoeEntity entity, ITextComponent name, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        if (entity.getAnimation() != Animation.YEARBOOK) {
            if (Minecraft.getInstance().player.getDistance(entity) > 8.0F) { return; }
            String[] lines = new String[] { this.getHealth(entity), name.getString(), entity.getDatabaseID().toString() };
            stack.push();
            stack.translate(0.0D, entity.getHeight() + 0.5F, 0.0D);
            stack.rotate(this.renderManager.getCameraOrientation());
            stack.scale(-0.025F, -0.025F, 0.025F);
            for (int i = 0; i < lines.length; ++i) {
                FontRenderer font = this.getFontRendererFromRenderManager();
                int x = -font.getStringWidth(lines[i]) / 2;
                int y = i * -10;
                Matrix4f matrix = stack.getLast().getMatrix();
                int alpha = (int) (Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F) * 255.0F) << 24;
                font.renderString(lines[i], x, y, 0xFFFFFFFF, false, matrix, buffer, false, alpha, packedLight);
            }
            stack.pop();
        }
    }

    public String getHealth(MoeEntity entity) {
        return String.format("%d / %d", (int) entity.getHealth(), (int) entity.getMaxHealth());
    }

    @Override
    public void preRenderCallback(MoeEntity entity, MatrixStack stack, float partialTickTime) {
        super.preRenderCallback(entity, stack, partialTickTime);
        entity.getAnimation().render(entity, stack, partialTickTime);
        stack.scale(0.9375F, 0.9375F, 0.9375F);
        if (entity.getAnimation() != Animation.YEARBOOK) {
            stack.scale(entity.getScale(), entity.getScale(), entity.getScale());
            this.shadowSize = entity.getScale() * 0.25F;
        }
    }
}
