package moeblocks.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.MoeMod;
import moeblocks.client.screen.widget.ChangePageButton;
import moeblocks.client.screen.widget.DeletePageButton;
import moeblocks.datingsim.CacheNPC;
import moeblocks.datingsim.DatingSim;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeEntities;
import moeblocks.init.MoeMessages;
import moeblocks.message.CRemovePageFromYearbook;
import moeblocks.util.Trans;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class YearbookScreen extends Screen {
    public static final ResourceLocation YEARBOOK_TEXTURES = new ResourceLocation(MoeMod.ID, "textures/gui/yearbook.png");
    private final String[] stats = new String[4];
    private final String[] lines = new String[4];
    private final DatingSim sim;
    private int index;
    private CacheNPC npc;
    private Button buttonPreviousPage;
    private Button buttonNextPage;
    private Button buttonRemovePage;
    private String name;
    private AbstractNPCEntity entity;

    public YearbookScreen(DatingSim sim, int index) {
        super(NarratorChatListener.EMPTY);
        this.sim = sim;
        this.index = index;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.renderPortrait(stack);
        this.renderEntity(this.width / 2 + 3, 90, 40.0F, this.entity);
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
        if (this.buttonRemovePage.isHovered()) { text.add(new TranslationTextComponent("gui.moeblocks.button.remove")); }
        if (102 < mouseY && mouseY < 112) {
            if (this.width / 2 - 50 < mouseX && mouseX < this.width / 2 - 24) { text.add(new TranslationTextComponent("gui.moeblocks.label.health")); }
            if (this.width / 2 - 24 < mouseX && mouseX < this.width / 2 + -2) { text.add(new TranslationTextComponent("gui.moeblocks.label.hunger")); }
            if (this.width / 2 + -2 < mouseX && mouseX < this.width / 2 + 24) { text.add(new TranslationTextComponent("gui.moeblocks.label.love")); }
            if (this.width / 2 + 24 < mouseX && mouseX < this.width / 2 + 50) { text.add(new TranslationTextComponent("gui.moeblocks.label.stress")); }
        }
        if (text.size() > 0) { this.renderTooltip(stack, Lists.transform(text, ITextComponent::func_241878_f), mouseX, mouseY); }
        if (this.entity != null) {
            this.blit(stack, (this.width - 60) / 2 + 2, 26, 160, 95, 60, 60);
        }
    }

    public void renderPortrait(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(YEARBOOK_TEXTURES);
        this.blit(stack, (this.width - 60) / 2 + 3, 27, 161, 25, 58, 58);
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
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else {
            switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT:
            case GLFW.GLFW_KEY_A:
                this.buttonPreviousPage.onPress();
                return true;
            case GLFW.GLFW_KEY_RIGHT:
            case GLFW.GLFW_KEY_D:
                this.buttonNextPage.onPress();
                return true;
            default:
                return false;
            }
        }
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 68, 196, 136, 20, DialogTexts.GUI_DONE, (button) -> this.minecraft.displayGuiScreen(null)));
        this.buttonPreviousPage = this.addButton(new ChangePageButton((this.width - 146) / 2 + 21, 51, -1, (button) -> {
            if (this.index > 0) { --this.index; }
            this.updateButtons();
        }));
        this.buttonNextPage = this.addButton(new ChangePageButton((this.width - 146) / 2 + 122, 51, 1, (button) -> {
            if (this.index < this.sim.size() - 1) { ++this.index; }
            this.updateButtons();
        }));
        this.buttonRemovePage = this.addButton(new DeletePageButton((this.width - 146) / 2 + 118, 12, (button) -> {
            MoeMessages.send(new CRemovePageFromYearbook(this.npc.getUUID()));
            this.closeScreen();
        }));
        this.updateButtons();
    }

    private void updateButtons() {
        if (this.sim.isEmpty()) {
            this.minecraft.player.sendStatusMessage(new TranslationTextComponent("gui.moeblocks.error.yearbook"), true);
            this.minecraft.displayGuiScreen(null);
        } else {
            this.npc = this.sim.get(this.index);
            this.entity = this.npc.get(this.minecraft.world, MoeEntities.MOE.get());
            this.name = this.entity.getFullName();
            this.stats[0] = String.format("%.0f", this.entity.getHealth());
            this.stats[1] = String.format("%.0f", this.entity.getHunger());
            this.stats[2] = String.format("%.0f", this.entity.getLove());
            this.stats[3] = String.format("%.0f", this.entity.getStress());
            this.lines[0] = String.format(Trans.late("gui.moeblocks.label.dere"), this.entity.getDere().toString());
            this.lines[1] = String.format(Trans.late("gui.moeblocks.label.blood"), this.entity.getBloodType().toString());
            this.lines[2] = String.format(Trans.late("gui.moeblocks.label.age"), this.entity.getAgeInYears());
            this.lines[3] = this.npc.isDead() ? Trans.late("debug.moeblocks.story.dead") : this.entity.getStory().toString();
            this.buttonPreviousPage.visible = this.index > 0;
            this.buttonNextPage.visible = this.index < this.sim.size() - 1;
            this.buttonRemovePage.visible = this.npc.isDead();
        }
    }

}