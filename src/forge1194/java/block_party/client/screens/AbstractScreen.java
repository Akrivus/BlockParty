package block_party.client.screens;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class AbstractScreen extends Screen {
    protected final int white = 0xffffff;
    protected final int zero = 0;
    protected final int sizeX;
    protected final int sizeY;

    protected AbstractScreen(int x, int y) {
        super(Component.empty());
        this.sizeX = x;
        this.sizeY = y;
    }

    public int getLeft(int margin) {
        return this.getLeft() + margin;
    }

    public int getLeft() {
        return this.getCenter(this.sizeX);
    }

    public int getCenter(int size) {
        return (this.width - size) / 2;
    }

    public int getRight(int margin) {
        return this.getRight() - margin;
    }

    public int getRight() {
        return this.getLeft() + this.sizeX;
    }

    public int getTop(int margin) {
        return this.getTop() + margin;
    }

    public int getTop() {
        return 0;
    }

    public int getBottom(int margin) {
        return this.getBottom() - margin;
    }

    public int getBottom() {
        return this.height - 24;
    }

    public int getAbsoluteCenter(int margin) {
        return this.getCenter(0) - margin;
    }

    public Player getPlayer() {
        return this.minecraft.player;
    }

    protected void renderEntity(int posX, int posY, float scale, LivingEntity entity) {
        if (entity == null) { return; }
        PoseStack pose = RenderSystem.getModelViewStack();
        pose.pushPose();
        this.setEntityViewStack(pose, posX, posY);
        RenderSystem.applyModelViewMatrix();
        PoseStack stack = new PoseStack();
        this.setEntityModelStack(stack, scale);
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher renderer = this.minecraft.getEntityRenderDispatcher();
        renderer.setRenderShadow(false);
        MultiBufferSource.BufferSource buffer = this.minecraft.renderBuffers().bufferSource();
        renderer.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, stack, buffer, 0xf000f0);
        buffer.endBatch();
        renderer.setRenderShadow(true);
        pose.popPose();
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    protected void setEntityViewStack(PoseStack pose, int posX, int posY) {

    }

    protected void setEntityModelStack(PoseStack pose, float scale) {

    }
}
