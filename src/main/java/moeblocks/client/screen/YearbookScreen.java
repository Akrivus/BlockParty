package moeblocks.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.MoeMod;
import moeblocks.automata.state.enums.*;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeEntities;
import moeblocks.init.MoeMessages;
import moeblocks.init.MoeSounds;
import moeblocks.message.CRemovePage;
import moeblocks.util.Trans;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YearbookScreen extends ControllerScreen {
    public static final ResourceLocation YEARBOOK_TEXTURES = new ResourceLocation(MoeMod.ID, "textures/gui/yearbook.png");
    private final String[] stats = new String[4];
    private final String[] lines = new String[4];
    private AbstractNPCEntity entity;
    private String name;
    private String page;
    private Button buttonPreviousPage;
    private Button buttonNextPage;
    private Button buttonRemovePage;
    
    public YearbookScreen(List<UUID> npcs, UUID uuid) {
        super(npcs, uuid, 146, 187);
        this.getNPC(uuid);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void setNPC() {
        this.entity = this.npc.clone(this.minecraft, MoeEntities.MOE.get());
        this.entity.setAnimation(Animation.YEARBOOK);
        this.name = this.entity.getFullName();
        this.updateButtons();
        this.stats[0] = String.format("%.0f", this.entity.getHealth());
        this.stats[1] = String.format("%.0f", this.entity.getFoodLevel());
        this.stats[2] = String.format("%.0f", this.entity.getLove());
        this.stats[3] = String.format("%.0f", this.entity.getStress());
        this.lines[0] = Trans.late(this.entity.getDere().getTranslationKey());
        this.lines[1] = Trans.late(this.entity.getBloodType().getTranslationKey());
        this.lines[2] = String.format(Trans.late("debug.moeblocks.age"), this.entity.getAgeInYears());
        if (this.npc.isEstranged()) {
            this.lines[3] = Trans.late(StoryPhase.ESTRANGED.getTranslationKey());
        } else if (this.npc.isDead()) {
            this.lines[3] = Trans.late(StoryPhase.DEAD.getTranslationKey());
        } else {
            this.lines[3] = Trans.late(this.entity.getStoryPhase().getTranslationKey());
        }
    }
    
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.npc == null) { return; }
        this.renderBackground(stack);
        this.renderPortrait(stack);
        this.renderEntity(this.getAbsoluteCenter(-3), 90, 40.0F, this.entity);
        if (this.npc.isRemovable()) { this.renderOverlay(stack); }
        this.renderBook(stack);
        this.font.drawString(stack, this.name, this.getCenter(this.font.getStringWidth(this.name)) + 3, 91, 0);
        this.font.drawString(stack, this.page, this.getRight(this.font.getStringWidth(this.page)) - 13, 11, 0);
        for (int x = 0; x < this.stats.length; ++x) {
            this.font.drawString(stack, this.stats[x], this.getAbsoluteCenter(38) + x * 25, 104, 0);
        }
        for (int y = 0; y < this.lines.length; ++y) {
            this.font.drawString(stack, this.lines[y], this.getAbsoluteCenter(40) - (y > 0 ? 5 : y), 123 + 10 * y, 0);
        }
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltips(stack, mouseX, mouseY);
    }
    
    public void renderTooltips(MatrixStack stack, int mouseX, int mouseY) {
        List<ITextComponent> text = new ArrayList<>();
        if (this.buttonRemovePage.isHovered()) {
            text.add(new TranslationTextComponent("gui.moeblocks.button.remove"));
        }
        if (102 < mouseY && mouseY < 112) {
            if (this.getAbsoluteCenter(50) < mouseX && mouseX < this.getAbsoluteCenter(24)) {
                text.add(new StringTextComponent(Trans.late(this.entity.getState(HealthState.class).getTranslationKey())));
            }
            if (this.getAbsoluteCenter(24) < mouseX && mouseX < this.getAbsoluteCenter(2)) {
                text.add(new StringTextComponent(Trans.late(this.entity.getState(HungerState.class).getTranslationKey())));
            }
            if (this.getAbsoluteCenter(2) < mouseX && mouseX < this.getAbsoluteCenter(-24)) {
                text.add(new StringTextComponent(Trans.late(this.entity.getState(LoveState.class).getTranslationKey())));
            }
            if (this.getAbsoluteCenter(-24) < mouseX && mouseX < this.getAbsoluteCenter(-50)) {
                text.add(new StringTextComponent(Trans.late(this.entity.getState(StressState.class).getTranslationKey())));
            }
        }
        if (text.size() > 0) {
            this.renderTooltip(stack, Lists.transform(text, ITextComponent::func_241878_f), mouseX, mouseY);
        }
    }
    
    public void renderPortrait(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(YEARBOOK_TEXTURES);
        this.blit(stack, this.getCenter(60) + 3, 27, 161, 25, 58, 58);
    }
    
    public void renderOverlay(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(YEARBOOK_TEXTURES);
        if (this.npc.isDead()) { this.blit(stack, this.getCenter(60) + 2, 26, 160, 155, 60, 60); }
        if (this.npc.isEstranged()) { this.blit(stack, this.getCenter(60) + 2, 26, 160, 95, 60, 60); }
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
    
    public void renderBook(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(YEARBOOK_TEXTURES);
        this.blit(stack, this.getCenter(146), 2, 0, 0, 146, 187);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) { return true; }
        switch (keyCode) {
        case GLFW.GLFW_KEY_RIGHT:
        case GLFW.GLFW_KEY_D:
            this.buttonNextPage.onPress();
            return true;
        case GLFW.GLFW_KEY_LEFT:
        case GLFW.GLFW_KEY_A:
            this.buttonPreviousPage.onPress();
            return true;
        default:
            return false;
        }
    }
    
    @Override
    protected void init() {
        this.addButton(new Button(this.getCenter(68), 196, 136, 20, DialogTexts.GUI_DONE, (button) -> this.minecraft.displayGuiScreen(null)));
        this.buttonNextPage = this.addButton(new TurnPageButton(this.getCenter(146) + 122, 51, 1, (button) -> {
            if (this.index + 1 < this.count) { this.getNPC(this.npcs.get(this.index + 1)); }
        }));
        this.buttonPreviousPage = this.addButton(new TurnPageButton(this.getCenter(146) + 21, 51, -1, (button) -> {
            if (this.index - 1 >= 0) { this.getNPC(this.npcs.get(this.index - 1)); }
        }));
        this.buttonRemovePage = this.addButton(new RemovePageButton(this.getCenter(146) + 115, 9, (button) -> {
            MoeMessages.send(new CRemovePage(this.npc.getUUID()));
            this.npcs.remove(this.npc.getUUID());
            if (++this.index >= this.count) { --this.index; }
            if (this.index < 0) { this.index = 0; }
            if (this.npcs.isEmpty()) {
                this.closeScreen();
            } else {
                this.getNPC(this.npcs.get(this.index));
            }
        }));
    }
    
    private void updateButtons() {
        if (this.npcs.isEmpty()) {
            this.minecraft.player.sendStatusMessage(new TranslationTextComponent("gui.moeblocks.error.empty"), true);
            this.closeScreen();
        } else {
            this.page = String.format(Trans.late("gui.moeblocks.label.page"), this.index + 1, this.count);
            this.buttonNextPage.visible = this.index + 1 < this.count;
            this.buttonPreviousPage.visible = this.index - 1 >= 0;
            this.buttonRemovePage.visible = this.npc.isRemovable();
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public class TurnPageButton extends Button {
        private final int delta;
        
        public TurnPageButton(int x, int y, int delta, IPressable button) {
            super(x, y, 7, 10, StringTextComponent.EMPTY, button);
            this.delta = delta;
        }
        
        @Override
        public void playDownSound(SoundHandler sound) {
            sound.play(SimpleSound.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
        }
        
        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(YEARBOOK_TEXTURES);
            int x = this.delta > 0 ? 226 : 147;
            int y = this.isHovered() ? 35 : 63;
            this.blit(stack, this.x, this.y, x, y, 7, 10);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public class RemovePageButton extends Button {
        public RemovePageButton(int x, int y, IPressable button) {
            super(x, y, 18, 18, StringTextComponent.EMPTY, button);
        }
        
        @Override
        public void playDownSound(SoundHandler sound) {
            sound.play(SimpleSound.master(MoeSounds.ITEM_YEARBOOK_REMOVE_PAGE.get(), 1.0F));
        }
        
        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(YEARBOOK_TEXTURES);
            int x = this.isHovered() ? 164 : 146;
            this.blit(stack, this.x, this.y, x, 7, 18, 18);
        }
    }
}
