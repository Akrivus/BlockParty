package moe.blocks.mod.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.blocks.mod.MoeMod;
import moe.blocks.mod.client.model.SenpaiModel;
import moe.blocks.mod.entity.SenpaiEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class SenpaiRenderer extends MobRenderer<SenpaiEntity, SenpaiModel> implements IRenderFactory<SenpaiEntity> {

    public SenpaiRenderer(EntityRendererManager manager) {
        super(manager, new SenpaiModel(), 0.5F);
        this.addLayer(new HeldItemLayer<>(this));
    }

    @Override
    public void preRenderCallback(SenpaiEntity entity, MatrixStack stack, float partialTickTime) {
        super.preRenderCallback(entity, stack, partialTickTime);
        stack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getEntityTexture(SenpaiEntity entity) {
        return new ResourceLocation(MoeMod.ID, String.format("textures/entity/senpai/%s.png", entity.getDere().toString().toLowerCase()));
    }

    @Override
    protected void renderName(SenpaiEntity entity, ITextComponent name, MatrixStack stack, IRenderTypeBuffer buffer, int packedLight) {
        String[] lines = new String[]{this.getHealth(entity), name.getString()};
        stack.push();
        stack.translate(0.0D, entity.getHeight() + 0.5F, 0.0D);
        stack.rotate(this.renderManager.getCameraOrientation());
        stack.scale(-0.025F, -0.025F, 0.025F);
        for (int i = 0; i < lines.length; ++i) {
            FontRenderer font = this.getFontRendererFromRenderManager();
            int x = -font.getStringWidth(lines[i]) / 2;
            int y = i * -10;
            int color = this.getColor(entity);
            Matrix4f matrix = stack.getLast().getMatrix();
            int alpha = (int) (Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F) * 255.0F) << 24;
            font.renderString(lines[i], x, y, color, false, matrix, buffer, false, alpha, packedLight);
        }
        stack.pop();
    }

    public String getHealth(SenpaiEntity entity) {
        int health = (int) Math.ceil(entity.getHealth());
        int max = (int) Math.ceil(entity.getMaxHealth());
        return String.format("%d / %d", health, max);
    }

    public int getColor(SenpaiEntity entity) {
        return entity.getDere().getNameColor();
    }

    @Override
    public MobRenderer<SenpaiEntity, SenpaiModel> createRenderFor(EntityRendererManager manager) {
        return new SenpaiRenderer(manager);
    }
}
