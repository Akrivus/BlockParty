package moeblocks.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.MoeMod;
import moeblocks.datingsim.CacheNPC;
import moeblocks.init.MoeMessages;
import moeblocks.init.MoeSounds;
import moeblocks.message.CNPCTeleport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CellPhoneScreen extends ControllerScreen {
    public static final ResourceLocation CELL_PHONE_TEXTURES = new ResourceLocation(MoeMod.ID, "textures/gui/cell_phone.png");
    private final List<ContactButton> contacts = new ArrayList<>();
    private int start;
    private int total;
    private Button buttonScrollUp;
    private Button buttonScrollDown;
    
    public CellPhoneScreen(List<UUID> npcs) {
        super(npcs, UUID.randomUUID(), 108, 182);
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
            this.contacts.add(new ContactButton(this, this.npc));
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
        this.buttonScrollUp = this.addButton(new ScrollButton(this, this.width / 2 + 37, 32, -1));
        this.buttonScrollDown = this.addButton(new ScrollButton(this, this.width / 2 + 37, 91, 1));
        this.updateButtons();
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
    
    @OnlyIn(Dist.CLIENT)
    public class ScrollButton extends Button {
        public ScrollButton(CellPhoneScreen parent, int x, int y, int delta) {
            super(x, y, 7, 7, StringTextComponent.EMPTY, (button) -> parent.setScroll(delta));
        }
        
        @Override
        public void playDownSound(SoundHandler sound) { }
        
        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(CELL_PHONE_TEXTURES);
            int x = this.isHovered() ? 116 : 108;
            this.blit(stack, this.x, this.y, x, 73, 7, 7);
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public static class ContactButton extends Button {
        public ContactButton(CellPhoneScreen parent, CacheNPC npc) {
            super(0, 0, 81, 15, new StringTextComponent(npc.getContactName()), (button) -> {
                MoeMessages.send(new CNPCTeleport(npc.getUUID()));
                parent.closeScreen();
            });
        }
        
        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int color = this.isHovered() ? 0xffffff : 0xff7fb6;
            Minecraft minecraft = Minecraft.getInstance();
            FontRenderer font = minecraft.fontRenderer;
            minecraft.getTextureManager().bindTexture(CELL_PHONE_TEXTURES);
            this.blit(stack, this.x, this.y, 108, this.isHovered() ? 98 : 115, 81, 15);
            font.drawString(stack, this.getMessage().getString(), this.x + 10, this.y + 4, color);
        }
        
        @Override
        public void playDownSound(SoundHandler sound) {
            sound.play(SimpleSound.master(MoeSounds.CELL_PHONE_BUTTON.get(), 1.0F));
        }
    }
}
