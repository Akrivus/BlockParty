package moeblocks.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.MoeMod;
import moeblocks.datingsim.convo.Dialogue;
import moeblocks.datingsim.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeEntities;
import moeblocks.init.MoeMessages;
import moeblocks.message.CNPCInteract;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.TranslationTextComponent;

public class DialogueScreen extends Screen {
    public static final ResourceLocation DIALOGUE_TEXTURES = new ResourceLocation(MoeMod.ID, "textures/gui/dialogue.png");
    private final Dialogue dialogue;
    private AbstractNPCEntity entity;
    private String name;
    private String line;
    
    public DialogueScreen(Dialogue dialogue) {
        super(NarratorChatListener.EMPTY);
        this.dialogue = dialogue;
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.renderEntity(0, 0, 1.0F, this.entity);
        this.renderDialogue(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
    
    public void renderEntity(int posX, int posY, float scale, LivingEntity entity) {
        if (entity == null) { return; }
        RenderSystem.pushMatrix();
        RenderSystem.translatef(posX, posY, 1050.0F);
        RenderSystem.scalef(-1.0F, -1.0F, -1.0F);
        MatrixStack stack = new MatrixStack();
        stack.translate(0.0D, 0.0D, 1000.0D);
        stack.scale(scale, scale, scale);
        stack.rotate(new Quaternion(0.0F, -1.0F, 0.0F, 0.0F));
        EntityRendererManager renderer = this.minecraft.getRenderManager();
        renderer.setRenderShadow(false);
        IRenderTypeBuffer.Impl buffer = this.minecraft.getRenderTypeBuffers().getBufferSource();
        renderer.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, stack, buffer, 0xf000f0);
        buffer.finish();
        renderer.setRenderShadow(true);
        RenderSystem.popMatrix();
    }
    
    public void renderDialogue(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(DIALOGUE_TEXTURES);
        this.blit(stack, (this.width - 146) / 2, 2, 0, 0, 146, 187);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) { return true; }
        switch (keyCode) {
            default:
                return false;
        }
    }
    
    @Override
    protected void init() {
        this.entity = this.dialogue.getEntity(MoeEntities.MOE.get());
        this.name = this.entity.getFullName();
        this.line = this.dialogue.getLine();
        int i = 0;
        for (Response response : Response.values()) {
            if (this.dialogue.has(response)) { this.addButton(new RespondButton(this, this.dialogue, response, ++i)); }
        }
    }
    
    public static class RespondButton extends Button {
        private final int index;
        
        public RespondButton(DialogueScreen parent, Dialogue dialogue, Response response, int index) {
            super(0, 0, 81, 15, NarratorChatListener.EMPTY, (button) -> {
                MoeMessages.send(new CNPCInteract(dialogue.getSpeaker().getUUID(), response));
                parent.closeScreen();
            }, (button, stack, x, y) -> parent.renderTooltip(stack, new TranslationTextComponent(response.getKey()), x, y));
            this.index = index;
        }
        
        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(DIALOGUE_TEXTURES);
            this.blit(stack, this.x, this.y, this.index * 15 + 5, this.isHovered() ? 58 : 48, 10, 10);
        }
    }
}
