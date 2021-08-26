package block_party.client.screen;

import block_party.BlockParty;
import block_party.db.records.NPC;
import block_party.init.BlockPartyMessages;
import block_party.init.BlockPartySounds;
import block_party.message.CNPCTeleport;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CellPhoneScreen extends ControllerScreen<NPC> {
    public static final ResourceLocation CELL_PHONE_TEXTURES = new ResourceLocation(BlockParty.ID, "textures/gui/cell_phone.png");
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
    public void setNPC() {
        if (this.npc.isDeadOrEstrangedFrom(this.getPlayer())) {
            this.npcs.remove(this.npc);
        } else {
            this.contacts.add(new ContactButton(this, this.npc));
        }
        ++this.total;
        this.updateButtons();
    }

    private void updateButtons() {
        if (this.npcs.size() == this.total && this.contacts.isEmpty()) {
            this.minecraft.player.displayClientMessage(new TranslatableComponent("gui.block_party.error.empty"), true);
            this.onClose();
        } else {
            this.contacts.forEach((contact) -> this.buttons.remove(contact));
            for (int y = this.start; y < Math.min(this.start + 4, this.contacts.size()); ++y) {
                Button button = this.contacts.get(y);
                button.x = this.getAbsoluteCenter(45);
                button.y = 32 + (y % 4) * 17;
                this.addButton(button);
            }
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.renderPhone(stack);
        this.renderScrollBar(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    public void renderPhone(PoseStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(CELL_PHONE_TEXTURES);
        this.blit(stack, this.getCenter(108), 2, 0, 0, 108, 182);
    }

    public void renderScrollBar(PoseStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(CELL_PHONE_TEXTURES);
        int y = (int) (Math.min((double) this.start / (this.contacts.size() - this.contacts.size() % 4), 1.0) * 35);
        this.blit(stack, this.getAbsoluteCenter(-37), 40 + y, 108, 82, 7, 15);
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
        this.addButton(new Button(this.getAbsoluteCenter(54), 190, 108, 20, CommonComponents.GUI_DONE, (button) -> this.onClose()));
        this.buttonScrollUp = this.addButton(new ScrollButton(this, this.getAbsoluteCenter(-37), 32, -1));
        this.buttonScrollDown = this.addButton(new ScrollButton(this, this.getAbsoluteCenter(-37), 91, 1));
        this.updateButtons();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (delta > 0) { this.buttonScrollUp.onPress(); }
        if (delta < 0) { this.buttonScrollDown.onPress(); }
        return delta != 0;
    }

    public void setScroll(int delta) {
        this.start += 4 * delta;
        int range = this.contacts.size() - 1;
        if (this.start < 0) { this.start = range - range % 4; }
        if (this.start > range) { this.start = 0; }
        this.updateButtons();
    }

    @OnlyIn(Dist.CLIENT)
    public static class ContactButton extends Button {
        public ContactButton(CellPhoneScreen parent, NPC npc) {
            super(0, 0, 81, 15, new TextComponent(npc.getName()), (button) -> {
                BlockPartyMessages.send(new CNPCTeleport(npc.getID()));
                parent.onClose();
            });
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
            int color = this.isHovered() ? 0xffffff : 0xff7fb6;
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;
            minecraft.getTextureManager().bindForSetup(CELL_PHONE_TEXTURES);
            this.blit(stack, this.x, this.y, 108, this.isHovered() ? 98 : 115, 81, 15);
            font.draw(stack, this.getMessage().getString(), this.x + 10, this.y + 4, color);
        }

        @Override
        public void playDownSound(SoundManager sound) {
            sound.play(SimpleSoundInstance.forUI(BlockPartySounds.ITEM_CELL_PHONE_BUTTON.get(), 1.0F));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class ScrollButton extends Button {
        public ScrollButton(CellPhoneScreen parent, int x, int y, int delta) {
            super(x, y, 7, 7, TextComponent.EMPTY, (button) -> parent.setScroll(delta));
        }

        @Override
        public void playDownSound(SoundManager sound) { }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindForSetup(CELL_PHONE_TEXTURES);
            int x = this.isHovered() ? 116 : 108;
            this.blit(stack, this.x, this.y, x, 73, 7, 7);
        }
    }
}
