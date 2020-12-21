package moeblocks.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.MoeMod;
import moeblocks.client.screen.widget.PhoneContactButton;
import moeblocks.client.screen.widget.PhoneScrollButton;
import moeblocks.datingsim.DatingSim;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class CellPhoneScreen extends Screen {
    public static final ResourceLocation CELL_PHONE_TEXTURES = new ResourceLocation(MoeMod.ID, "textures/gui/cell_phone.png");
    protected final List<PhoneContactButton> contacts = new ArrayList<>();
    protected int selectedIndex = 0;
    protected int selectedStart;
    protected Button scrollUp;
    protected Button scrollDown;

    public CellPhoneScreen(DatingSim sim) {
        super(NarratorChatListener.EMPTY);
        sim.characters.forEach((uuid, npc) -> this.contacts.add(new PhoneContactButton(this, npc, this.contacts.size())));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta > 0) { this.scrollUp.onPress(); }
        if (delta < 0) { this.scrollDown.onPress(); }
        return delta != 0;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.renderPhone(stack);
        this.renderScrollBar(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) { return true; }
        switch (keyCode) {
        case GLFW.GLFW_KEY_UP:
        case GLFW.GLFW_KEY_W:
            this.scrollUp.onPress();
            return true;
        case GLFW.GLFW_KEY_DOWN:
        case GLFW.GLFW_KEY_S:
            this.scrollDown.onPress();
            return true;
        default:
            return false;
        }
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 54, 190, 108, 20, DialogTexts.GUI_DONE, (button) -> this.closeScreen()));
        this.scrollUp = this.addButton(new PhoneScrollButton(this, this.width / 2 + 37, 32, -1));
        this.scrollDown = this.addButton(new PhoneScrollButton(this, this.width / 2 + 37, 91, 1));
        this.updateButtons();
    }

    public void setSelected(int index) {
        if (index < 0) { index = this.contacts.size() + index; }
        this.selectedIndex = this.contacts.isEmpty() ? 0 : index % this.contacts.size();
        int shift = this.selectedIndex - this.selectedStart;
        if (shift < 0 || shift > 3) { this.setScroll(1, false); }
        this.updateButtons();
    }

    public boolean isSelected(int index) {
        return this.selectedIndex == index;
    }

    public void setScroll(int delta, boolean select) {
        this.selectedStart += 4 * delta;
        int range = this.contacts.size() - 1;
        if (this.selectedStart < 0) { this.selectedStart = range - range % 4; }
        if (this.selectedStart > range) { this.selectedStart = 0; }
        if (select) { this.setSelected(this.selectedStart); }
        this.updateButtons();
    }

    private void updateButtons() {
        if (this.contacts.isEmpty()) {
            this.minecraft.player.sendStatusMessage(new TranslationTextComponent("gui.moeblocks.error.cell_phone"), true);
            this.minecraft.displayGuiScreen(null);
        } else {
            this.contacts.forEach((contact) -> this.buttons.remove(contact));
            for (int y = this.selectedStart; y < Math.min(this.selectedStart + 4, this.contacts.size()); ++y) {
                Button button = this.contacts.get(y);
                button.x = this.width / 2 - 45;
                button.y = 32 + (y % 4) * 17;
                this.addButton(button);
            }
        }
    }

    public void renderPhone(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CELL_PHONE_TEXTURES);
        this.blit(stack, (this.width - 108) / 2, 2, 0, 0, 108, 182);
    }

    public void renderScrollBar(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CELL_PHONE_TEXTURES);
        int y = (int) (Math.min((double) this.selectedStart / (this.contacts.size() - this.contacts.size() % 4), 1.0) * 35);
        this.blit(stack, this.width / 2 + 37, 40 + y, 108, 82, 7, 15);
    }
}
