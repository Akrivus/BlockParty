package moe.blocks.mod.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.blocks.mod.MoeMod;
import moe.blocks.mod.data.Yearbooks;
import moe.blocks.mod.entity.partial.CharacterEntity;
import moe.blocks.mod.init.MoeMessages;
import moe.blocks.mod.init.MoeSounds;
import moe.blocks.mod.message.CRemovePageFromYearbook;
import moe.blocks.mod.util.Trans;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class YearbookScreen extends Screen {
    public static final ResourceLocation YEARBOOK_TEXTURES = new ResourceLocation(MoeMod.ID, "textures/gui/yearbook.png");
    private final String[] stats = new String[4];
    private final String[] lines = new String[4];
    private final Yearbooks.Book book;
    private int pageNumber;
    private Yearbooks.Page page;
    private Button buttonPreviousPage;
    private Button buttonNextPage;
    private Button buttonRemovePage;
    private String name;
    private CharacterEntity entity;

    public YearbookScreen(Yearbooks.Book book, int pageNumber) {
        super(NarratorChatListener.EMPTY);
        this.book = book;
        this.pageNumber = pageNumber;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.renderPortrait(stack);
        this.renderEntity(this.width / 2 + 3, 90, 40.0F, this.entity);
        this.renderBook(stack);
        this.font.drawString(stack, this.name, (this.width - this.font.getStringWidth(this.name)) / 2 + 3, 92, 0);
        for (int x = 0; x < this.stats.length; ++x) {
            this.font.drawString(stack, this.stats[x], this.width / 2 - 38 + x * 25, 105, 0);
        }
        for (int y = 0; y < this.lines.length; ++y) {
            this.font.drawString(stack, this.lines[y], this.width / 2 - 40 - (y > 0 ? 5 : y), 124 + 10 * y, 0);
        }
        this.renderTooltips(stack, mouseX, mouseY);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    public void renderTooltips(MatrixStack stack, int mouseX, int mouseY) {
        List<ITextComponent> text = new ArrayList<>();
        if (this.buttonRemovePage.isHovered()) { text.add(new TranslationTextComponent("gui.moeblocks.label.remove")); }
        if (102 < mouseY && mouseY < 112) {
            if (this.width / 2 - 50 < mouseX && mouseX < this.width / 2 - 24) { text.add(new TranslationTextComponent("gui.moeblocks.label.health")); }
            if (this.width / 2 - 24 < mouseX && mouseX < this.width / 2 + -2) { text.add(new TranslationTextComponent("gui.moeblocks.label.hunger")); }
            if (this.width / 2 + -2 < mouseX && mouseX < this.width / 2 + 24) { text.add(new TranslationTextComponent("gui.moeblocks.label.love")); }
            if (this.width / 2 + 24 < mouseX && mouseX < this.width / 2 + 50) { text.add(new TranslationTextComponent("gui.moeblocks.label.stress")); }
        }
        if (text.size() > 0) { this.renderTooltip(stack, Lists.transform(text, ITextComponent::func_241878_f), mouseX, mouseY); }
        if (entity != null && entity.isDead()) {
            this.blit(stack, (this.width - 60) / 2 + 2, 27, 182, 96, 60, 60);
        }
    }

    public void renderPortrait(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(YEARBOOK_TEXTURES);
        this.blit(stack, (this.width - 60) / 2 + 3, 28, 183, 26, 58, 58);
    }

    public void renderBook(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(YEARBOOK_TEXTURES);
        this.blit(stack, (this.width - 146) / 2, 2, 20, 0, 146, 188);
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
        this.buttonPreviousPage = this.addButton(new ChangePageButton((this.width - 146) / 2 + 21, 52, -1, (button) -> {
            if (this.pageNumber > 0) { --this.pageNumber; }
            this.updateButtons();
        }));
        this.buttonNextPage = this.addButton(new ChangePageButton((this.width - 146) / 2 + 122, 52, 1, (button) -> {
            if (this.pageNumber < this.book.getPageCount() - 1) { ++this.pageNumber; }
            this.updateButtons();
        }));
        this.buttonRemovePage = this.addButton(new RemovePageButton((this.width - 146) / 2 + 118, 13, (button) -> {
            MoeMessages.send(new CRemovePageFromYearbook(this.page.getUUID()));
            this.closeScreen();
        }));
        this.updateButtons();
    }

    private void updateButtons() {
        this.page = this.book.getPage(this.pageNumber);
        this.entity = this.page.getCharacter(this.minecraft);
        this.name = this.page.getName(this.entity);
        this.stats[0] = String.format("%.0f", this.page.getHealth());
        this.stats[1] = String.format("%.0f", this.page.getHunger());
        this.stats[2] = String.format("%.0f", this.page.getLove());
        this.stats[3] = String.format("%.0f", this.page.getStress());
        this.lines[0] = String.format(Trans.late("gui.moeblocks.label.dere"), this.page.getDere().toString());
        this.lines[1] = String.format(Trans.late("gui.moeblocks.label.blood"), this.page.getBloodType().toString());
        this.lines[2] = String.format(Trans.late("gui.moeblocks.label.age"), this.page.getAge());
        this.lines[3] = String.format(Trans.late("gui.moeblocks.label.status"), this.page.getStatus().toString());
        this.buttonPreviousPage.visible = this.pageNumber > 0;
        this.buttonNextPage.visible = this.pageNumber < this.book.getPageCount() - 1;
    }

    public void renderEntity(int posX, int posY, float scale, LivingEntity entity) {
        if (entity == null) { return; }
        Quaternion forward = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion lateral = Vector3f.XP.rotationDegrees(0.0F);
        forward.multiply(lateral);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(posX, posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack stack = new MatrixStack();
        stack.translate(0.0D, 0.0D, 1000.0D);
        stack.scale(scale, scale, scale);
        stack.rotate(forward);
        EntityRendererManager renderer = this.minecraft.getRenderManager();
        renderer.setCameraOrientation(lateral);
        renderer.setRenderShadow(false);
        IRenderTypeBuffer.Impl buffer = this.minecraft.getRenderTypeBuffers().getBufferSource();
        renderer.renderEntityStatic(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, stack, buffer, 0xf000f0);
        buffer.finish();
        renderer.setRenderShadow(true);
        RenderSystem.popMatrix();
    }

    @OnlyIn(Dist.CLIENT)
    public class ChangePageButton extends Button {
        private final int delta;

        public ChangePageButton(int x, int y, int delta, Button.IPressable button) {
            super(x, y, 7, 10, StringTextComponent.EMPTY, button);
            this.delta = delta;
        }

        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(YearbookScreen.YEARBOOK_TEXTURES);
            int x = this.delta > 0 ? 248 : 169;
            int y = this.isHovered() ? 36 : 64;
            this.blit(stack, this.x, this.y, x, y, 7, 10);
        }

        @Override
        public void playDownSound(SoundHandler sound) {
            sound.play(SimpleSound.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class RemovePageButton extends Button {
        public RemovePageButton(int x, int y, Button.IPressable button) {
            super(x, y, 13, 13, StringTextComponent.EMPTY, button);
        }

        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(YearbookScreen.YEARBOOK_TEXTURES);
            int x = this.isHovered() ? 169 : 138;
            this.blit(stack, this.x, this.y, x, 11, 13, 13);
        }

        @Override
        public void playDownSound(SoundHandler sound) {
            sound.play(SimpleSound.master(MoeSounds.YEARBOOK_REMOVE_PAGE.get(), 1.0F));
        }
    }
}
