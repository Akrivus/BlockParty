package moe.blocks.mod.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.blocks.mod.MoeMod;
import moe.blocks.mod.init.MoeMessages;
import moe.blocks.mod.init.MoeSounds;
import moe.blocks.mod.message.CPhoneRemoveMoe;
import moe.blocks.mod.message.CPhoneTeleportMoe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.UUID;

public class CellPhoneScreen extends Screen {
    public static final ResourceLocation CELL_PHONE_TEXTURES = new ResourceLocation(MoeMod.ID, "textures/gui/cell_phone.png");
    protected final List<ContactEntry> contacts;
    protected int lastSelected;
    protected int selected = 0;
    protected int skip;
    protected Button buttonScrollDown;
    protected Button buttonScrollUp;
    protected Button buttonAccept;
    protected Button buttonMenu;
    protected Button buttonSelect;
    protected Button buttonDelete;
    protected Button buttonDecline;

    public CellPhoneScreen(List<ContactEntry> contacts) {
        super(NarratorChatListener.EMPTY);
        this.contacts = contacts;
        this.contacts.forEach(contact -> contact.init(this));
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
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else {
            switch (keyCode) {
            case GLFW.GLFW_KEY_DOWN:
            case GLFW.GLFW_KEY_S:
                this.buttonScrollDown.onPress();
                return true;
            case GLFW.GLFW_KEY_UP:
            case GLFW.GLFW_KEY_W:
                this.buttonScrollUp.onPress();
                return true;
            default:
                return false;
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta < 0) { this.buttonScrollDown.onPress(); }
        if (delta > 0) { this.buttonScrollUp.onPress(); }
        return delta != 0;
    }

    @Override
    protected void init() {
        this.addButton(new Button(this.width / 2 - 54, 190, 108, 20, DialogTexts.GUI_DONE, (button) -> this.minecraft.displayGuiScreen(null)));
        this.buttonScrollDown = this.addButton(new ScrollButton(this, this.width / 2 + 37, 91, 1));
        this.buttonScrollUp = this.addButton(new ScrollButton(this, this.width / 2 + 37, 32, -1));
        this.buttonSelect = this.addButton(new PhoneButton(this.width / 2 + -8, 105, 2, (button) -> this.setSelected(this.selected + 1)));
        this.buttonDelete = this.addButton(new PhoneButton(this.width / 2 + 30, 105, 3, (button) -> this.setSelected(this.selected - 1)));
        this.buttonMenu = this.addButton(new PhoneButton(this.width / 2 - 27, 105, 1, (button) -> this.setSelected(0)));
        this.buttonAccept = this.addButton(new PhoneButton(this.width / 2 - 46, 105, 0, (button) -> {
            if (this.contacts.size() > 0) { MoeMessages.send(new CPhoneTeleportMoe(this.contacts.get(this.selected).uuid)); }
            Minecraft.getInstance().displayGuiScreen(null);
        }));
        this.buttonDecline = this.addButton(new PhoneButton(this.width / 2 + 11, 105, 4, (button) -> {
            if (this.contacts.size() > 0) { MoeMessages.send(new CPhoneRemoveMoe(this.contacts.remove(this.selected).uuid)); }
            this.setSelected(this.selected - 1);
            this.updateButtons();
        }));
        this.updateButtons();
    }

    private void updateButtons() {
        this.contacts.forEach((contact) -> this.buttons.remove(contact.button));
        for (int y = this.skip; y < Math.min(this.skip + 4, this.contacts.size()); ++y) {
            Button button = this.contacts.get(y).button;
            button.x = this.width / 2 - 45;
            button.y = 32 + (y % 4) * 17;
            this.addButton(button);
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
        int y = (int)(Math.min((double) this.skip / (this.contacts.size() - this.contacts.size() % 4), 1.0) * 35);
        this.blit(stack, this.width / 2 + 37, 40 + y, 108, 82, 7, 15);
    }

    public void setSelected(int index) {
        this.lastSelected = this.selected;
        this.selected = index % this.contacts.size();
        int shift = this.selected - this.skip;
        if (shift < 0 || shift > 3) {
            this.setScroll(1);
        } else {
            this.updateButtons();
        }
    }

    public void setScroll(int delta) {
        this.skip += 4 * delta;
        int range = this.contacts.size() - 1;
        if (this.skip < 0) { this.skip = range - range % 4; }
        if (this.skip > range) { this.skip = 0; }
        this.updateButtons();
    }

    @OnlyIn(Dist.CLIENT)
    public class PhoneButton extends Button {
        private final int index;

        public PhoneButton(int x, int y, int index, Button.IPressable button) {
            super(x, y, 15, 11, StringTextComponent.EMPTY, button);
            this.index = index;
        }

        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(CELL_PHONE_TEXTURES);
            int x = 8 + this.index * 19;
            int y = this.isHovered() ? 183 : 103;
            this.blit(stack, this.x, this.y, x, y, 15, 11);
        }

        @Override
        public void playDownSound(SoundHandler sound) {
            sound.play(SimpleSound.master(MoeSounds.CELL_PHONE_BUTTON.get(), 1.0F));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class ScrollButton extends Button {
        public ScrollButton(CellPhoneScreen parent, int x, int y, int delta) {
            super(x, y, 7, 7, StringTextComponent.EMPTY, (button) -> {
                parent.setScroll(delta);
                parent.setSelected(parent.skip);
            });
        }

        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(CELL_PHONE_TEXTURES);
            int x = this.isHovered() ? 116 : 108;
            this.blit(stack, this.x, this.y, x, 73, 7, 7);
        }

        @Override
        public void playDownSound(SoundHandler sound) { }
    }

    public static class ContactEntry {
        protected String name;
        protected UUID uuid;
        protected Button button;

        public ContactEntry(INBT tag) {
            CompoundNBT compound = (CompoundNBT) tag;
            this.name = compound.getString("Name");
            this.uuid = compound.getUniqueId("UUID");
        }

        public void init(CellPhoneScreen screen) {
            this.button = new ContactButton(screen, screen.contacts.indexOf(this), this);
        }

        @OnlyIn(Dist.CLIENT)
        public class ContactButton extends Button {
            private final CellPhoneScreen parent;
            private final int index;
            private final ContactEntry contact;

            public ContactButton(CellPhoneScreen parent, int index, ContactEntry contact) {
                super(0, 0, 81, 15, StringTextComponent.EMPTY, (button) -> {
                    MoeMessages.send(new CPhoneTeleportMoe(contact.uuid));
                    Minecraft.getInstance().displayGuiScreen(null);
                });
                this.parent = parent;
                this.index = index;
                this.contact = contact;
            }

            @Override
            public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                Minecraft.getInstance().getTextureManager().bindTexture(CELL_PHONE_TEXTURES);
                int y = this.isHovered() ? 98 : 115;
                this.blit(stack, this.x, this.y, 108, y, 81, 15);
                Minecraft.getInstance().fontRenderer.drawString(stack, this.contact.name, this.x + 10, this.y + 4, this.isHovered() ? 0xffffff : 0);
            }

            @Override
            public void playDownSound(SoundHandler sound) { }

            @Override
            public boolean isHovered() {
                return super.isHovered() || this.parent.selected == this.index;
            }
        }
    }
}
