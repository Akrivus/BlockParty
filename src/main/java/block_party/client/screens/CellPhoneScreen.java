package block_party.client.screens;

import block_party.BlockParty;
import block_party.client.ClientTranslations;
import block_party.network.payload.NpcCallPayload;
import block_party.network.payload.NpcCallRequestPayload;
import block_party.network.payload.NpcDetailPayload;
import block_party.network.payload.NpcDetailRequestPayload;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class CellPhoneScreen extends ControllerScreen {
    public static final ResourceLocation CELL_PHONE_TEXTURE = BlockParty.source("textures/gui/cell_phone.png");
    private static final int WIDTH = 108;
    private static final int HEIGHT = 182;

    private final Map<Long, NpcDetailPayload> contacts = new LinkedHashMap<>();
    private final List<Button> contactButtons = new ArrayList<>();
    private int loaded;
    private int start;
    private Button scrollUp;
    private Button scrollDown;

    public CellPhoneScreen(List<Long> databaseIds) {
        super(databaseIds, -1L);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.absoluteCenter(54), 190, WIDTH, 20)
                .build());
        this.scrollUp = this.addRenderableWidget(new ScrollButton(this.absoluteCenter(-37), 32, -1, button -> this.scroll(-1)));
        this.scrollDown = this.addRenderableWidget(new ScrollButton(this.absoluteCenter(-37), 91, 1, button -> this.scroll(1)));
        this.requestContacts();
        this.rebuildContacts();
    }

    @Override
    public void handleNpcDetail(NpcDetailPayload payload) {
        if (!this.databaseIds.contains(payload.databaseId())) {
            return;
        }
        ++this.loaded;
        if (payload.found() && !payload.dead() && !payload.hiding()) {
            this.contacts.put(payload.databaseId(), payload);
        }
        if (this.loaded >= this.databaseIds.size() && this.contacts.isEmpty()) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.translatable("gui.block_party.error.empty"), true);
            }
            this.onClose();
            return;
        }
        this.rebuildContacts();
    }

    @Override
    public void handleNpcCall(NpcCallPayload payload) {
        if (payload.success()) {
            this.onClose();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, CELL_PHONE_TEXTURE, this.left(WIDTH), 2, 0.0F, 0.0F, WIDTH, HEIGHT, 256, 256);
        this.renderScrollBar(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderScrollBar(GuiGraphics graphics) {
        int denominator = Math.max(1, this.contacts.size() - this.contacts.size() % 4);
        int y = (int) (Math.min((double) this.start / denominator, 1.0D) * 35.0D);
        graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, CELL_PHONE_TEXTURE, this.absoluteCenter(-37), 40 + y, 108, 82, 7, 15, 256, 256);
    }

    private void requestContacts() {
        this.loaded = 0;
        this.contacts.clear();
        for (long id : this.databaseIds) {
            PacketDistributor.sendToServer(new NpcDetailRequestPayload(id));
        }
        if (this.databaseIds.isEmpty()) {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.displayClientMessage(Component.translatable("gui.block_party.error.empty"), true);
            }
            this.onClose();
        }
    }

    private void rebuildContacts() {
        for (Button button : this.contactButtons) {
            this.removeWidget(button);
        }
        this.contactButtons.clear();
        List<NpcDetailPayload> values = List.copyOf(this.contacts.values());
        for (int offset = 0; offset < 4 && this.start + offset < values.size(); ++offset) {
            NpcDetailPayload npc = values.get(this.start + offset);
            Button button = new ContactButton(this.absoluteCenter(45), 32 + offset * 17, npc, value -> this.call(npc.databaseId()));
            this.contactButtons.add(this.addRenderableWidget(button));
        }
        if (this.scrollUp != null) {
            this.scrollUp.visible = this.contacts.size() > 4;
        }
        if (this.scrollDown != null) {
            this.scrollDown.visible = this.contacts.size() > 4;
        }
    }

    private void scroll(int delta) {
        if (this.contacts.isEmpty()) {
            this.start = 0;
            return;
        }
        this.start += 4 * delta;
        int range = this.contacts.size() - 1;
        if (this.start < 0) {
            this.start = range - range % 4;
        }
        if (this.start > range) {
            this.start = 0;
        }
        this.rebuildContacts();
    }

    private void call(long databaseId) {
        this.play(BlockParty.source("item.cell_phone.button"));
        PacketDistributor.sendToServer(new NpcCallRequestPayload(databaseId));
    }

    private void play(ResourceLocation soundId) {
        if (this.minecraft == null) {
            return;
        }
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(soundId);
        if (sound != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP || keyCode == GLFW.GLFW_KEY_W) {
            this.scroll(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN || keyCode == GLFW.GLFW_KEY_S) {
            this.scroll(1);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0) {
            this.scroll(-1);
        } else if (scrollY < 0) {
            this.scroll(1);
        }
        return scrollY != 0.0D;
    }

    private static class ContactButton extends Button {
        ContactButton(int x, int y, NpcDetailPayload npc, OnPress onPress) {
            super(x, y, 81, 15, ClientTranslations.displayName(npc), onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, CELL_PHONE_TEXTURE, this.getX(), this.getY(), 108, this.isHoveredOrFocused() ? 98 : 115, this.width, this.height, 256, 256);
            int color = this.isHoveredOrFocused() ? 0xFFFFFF : 0xFF7FB6;
            graphics.drawString(net.minecraft.client.Minecraft.getInstance().font, this.getMessage(), this.getX() + 10, this.getY() + 4, color, false);
        }
    }

    private static class ScrollButton extends Button {
        ScrollButton(int x, int y, int delta, OnPress onPress) {
            super(x, y, 7, 7, Component.literal(String.valueOf(delta)), onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int textureX = this.isHoveredOrFocused() ? 116 : 108;
            graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, CELL_PHONE_TEXTURE, this.getX(), this.getY(), textureX, 73, this.width, this.height, 256, 256);
        }
    }
}
