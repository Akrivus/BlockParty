package moeblocks.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.MoeMod;
import moeblocks.client.screen.widget.PhoneContactButton;
import moeblocks.client.screen.widget.PhoneScrollButton;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CellPhoneScreen extends ControllerScreen {
    public static final ResourceLocation CELL_PHONE_TEXTURES = new ResourceLocation(MoeMod.ID, "textures/gui/cell_phone.png");
    private final List<PhoneContactButton> contacts = new ArrayList<>();
    private int start;
    private int total;
    private Button buttonScrollUp;
    private Button buttonScrollDown;
    
    public CellPhoneScreen(List<UUID> npcs) {
        super(npcs, UUID.randomUUID());
        this.npcs.forEach(this::getNPC);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void setNPC() {
        if (this.npc.isRemovable()) {
            this.npcs.remove(this.npc);
        } else {
            this.contacts.add(new PhoneContactButton(this, this.npc));
        }
        ++this.total;
        this.updateButtons();
    }
    
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.renderPhone(stack);
        this.renderScrollBar(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
    
    public void renderPhone(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CELL_PHONE_TEXTURES);
        this.blit(stack, (this.width - 108) / 2, 2, 0, 0, 108, 182);
    }
    
    public void renderScrollBar(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CELL_PHONE_TEXTURES);
        int y = (int) (Math.min((double) this.start / (this.contacts.size() - this.contacts.size() % 4), 1.0) * 35);
        this.blit(stack, this.width / 2 + 37, 40 + y, 108, 82, 7, 15);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta > 0) { this.buttonScrollUp.onPress(); }
        if (delta < 0) { this.buttonScrollDown.onPress(); }
        return delta != 0;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) { return true; }
        switch (keyCode) {
            case GLFW.GLFW_KEY_UP:
            case GLFW.GLFW_KEY_W:
                this.buttonScrollUp.onPress();
                return true;
            case GLFW.GLFW_KEY_DOWN:
            case GLFW.GLFW_KEY_S:
                this.buttonScrollDown.onPress();
                return true;
            default:
                return false;
        }
    }
    
    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 54, 190, 108, 20, DialogTexts.GUI_DONE, (button) -> this.closeScreen()));
        this.buttonScrollUp = this.addButton(new PhoneScrollButton(this, this.width / 2 + 37, 32, -1));
        this.buttonScrollDown = this.addButton(new PhoneScrollButton(this, this.width / 2 + 37, 91, 1));
        this.updateButtons();
    }
    
    public boolean isSelected(int index) {
        return this.index == index;
    }
    
    public void setScroll(int delta) {
        this.start += 4 * delta;
        int range = this.contacts.size() - 1;
        if (this.start < 0) { this.start = range - range % 4; }
        if (this.start > range) { this.start = 0; }
        this.updateButtons();
    }
    
    private void updateButtons() {
        if (this.npcs.size() == this.total && this.contacts.isEmpty()) {
            this.minecraft.player.sendStatusMessage(new TranslationTextComponent("gui.moeblocks.error.empty"), true);
            this.closeScreen();
        } else {
            this.contacts.forEach((contact) -> this.buttons.remove(contact));
            for (int y = this.start; y < Math.min(this.start + 4, this.contacts.size()); ++y) {
                Button button = this.contacts.get(y);
                button.x = this.width / 2 - 45;
                button.y = 32 + (y % 4) * 17;
                this.addButton(button);
            }
        }
    }
}
