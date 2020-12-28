package moeblocks.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.MoeMod;
import moeblocks.automata.state.enums.Animation;
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
        this.stats[0] = String.format("%.0f", this.entity.getHealth());
        this.stats[1] = String.format("%.0f", this.entity.getFoodLevel());
        this.stats[2] = String.format("%.0f", this.entity.getLove());
        this.stats[3] = String.format("%.0f", this.entity.getStress());
        this.lines[0] = String.format(Trans.late("gui.moeblocks.label.dere"), this.entity.getDere().toString());
        this.lines[1] = String.format(Trans.late("gui.moeblocks.label.blood"), this.entity.getBloodType().toString());
        this.lines[2] = String.format(Trans.late("gui.moeblocks.label.age"), this.entity.getAgeInYears());
        this.lines[3] = this.entity.getStory().toString();
        if (this.npc.isDead()) { this.lines[3] = Trans.late("debug.moeblocks.story.dead"); }
        if (this.npc.isEstranged()) { this.lines[3] = Trans.late("debug.moeblocks.story.estranged"); }
        this.name = this.entity.getFullName();
        this.entity.setAnimation(Animation.YEARBOOK);
        this.updateButtons();
    }
    
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.npc == null) { return; }
        this.renderBackground(stack);
        this.renderPortrait(stack);
        this.renderEntity(this.width / 2 + 3, 90, 40.0F, this.entity);
        if (this.npc.isRemovable()) { this.renderOverlay(stack); }
        this.renderBook(stack);
        this.font.drawString(stack, this.name, (this.width - this.font.getStringWidth(this.name)) / 2 + 3, 91, 0);
        for (int x = 0; x < this.stats.length; ++x) {
            this.font.drawString(stack, this.stats[x], this.width / 2 - 38 + x * 25, 104, 0);
        }
        for (int y = 0; y < this.lines.length; ++y) {
            this.font.drawString(stack, this.lines[y], this.width / 2 - 40 - (y > 0 ? 5 : y), 123 + 10 * y, 0);
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
            if (this.width / 2 - 50 < mouseX && mouseX < this.width / 2 - 24) {
                text.add(new TranslationTextComponent("gui.moeblocks.label.health"));
            }
            if (this.width / 2 - 24 < mouseX && mouseX < this.width / 2 + -2) {
                text.add(new TranslationTextComponent("gui.moeblocks.label.foodLevel"));
            }
            if (this.width / 2 + -2 < mouseX && mouseX < this.width / 2 + 24) {
                text.add(new TranslationTextComponent("gui.moeblocks.label.love"));
            }
            if (this.width / 2 + 24 < mouseX && mouseX < this.width / 2 + 50) {
                text.add(new TranslationTextComponent("gui.moeblocks.label.stress"));
            }
        }
        if (text.size() > 0) {
            this.renderTooltip(stack, Lists.transform(text, ITextComponent::func_241878_f), mouseX, mouseY);
        }
    }
    
    public void renderPortrait(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(YEARBOOK_TEXTURES);
        this.blit(stack, (this.width - 60) / 2 + 3, 27, 161, 25, 58, 58);
    }
    
    public void renderOverlay(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(YEARBOOK_TEXTURES);
        if (this.npc.isDead()) { this.blit(stack, (this.width - 60) / 2 + 2, 26, 160, 155, 60, 60); }
        if (this.npc.isEstranged()) { this.blit(stack, (this.width - 60) / 2 + 2, 26, 160, 95, 60, 60); }
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
        this.blit(stack, (this.width - 146) / 2, 2, 0, 0, 146, 187);
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
        this.addButton(new Button(this.width / 2 - 68, 196, 136, 20, DialogTexts.GUI_DONE, (button) -> this.minecraft.displayGuiScreen(null)));
        this.buttonNextPage = this.addButton(new TurnPageButton((this.width - 146) / 2 + 122, 51, 1, (button) -> {
            if (this.index + 1 < this.npcs.size()) { this.getNPC(this.npcs.get(this.index + 1)); }
        }));
        this.buttonPreviousPage = this.addButton(new TurnPageButton((this.width - 146) / 2 + 21, 51, -1, (button) -> {
            if (this.index - 1 >= 0) { this.getNPC(this.npcs.get(this.index - 1)); }
        }));
        this.buttonRemovePage = this.addButton(new RemovePageButton((this.width - 146) / 2 + 115, 9, (button) -> {
            MoeMessages.send(new CRemovePage(this.npc.getUUID()));
            this.npcs.remove(this.npc.getUUID());
            if (++this.index >= this.npcs.size()) { --this.index; }
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
            this.buttonNextPage.visible = this.index + 1 < this.npcs.size();
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
            sound.play(SimpleSound.master(MoeSounds.YEARBOOK_REMOVE_PAGE.get(), 1.0F));
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
