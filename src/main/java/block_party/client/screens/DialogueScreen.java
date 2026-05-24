package block_party.client.screens;

import block_party.BlockParty;
import block_party.entities.Moe;
import block_party.network.payload.DialogueClosePayload;
import block_party.network.payload.DialogueOpenPayload;
import block_party.network.payload.DialogueRespondPayload;
import block_party.network.payload.NpcDetailPayload;
import block_party.registry.CustomEntities;
import block_party.scene.Dialogue;
import block_party.scene.Response;
import block_party.scene.Speaker;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntitySpawnReason;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class DialogueScreen extends Screen {
    public static final ResourceLocation DIALOGUE_TEXTURE = BlockParty.source("textures/gui/dialogue.png");
    private static final int WIDTH = 242;
    private static final int HEIGHT = 48;
    private static final int SPEAKER_PREVIEW_HALF_WIDTH = 55;
    private static final int SPEAKER_PREVIEW_TOP_OFFSET = 150;
    private static final int SPEAKER_PREVIEW_BOTTOM_OFFSET = 18;
    private static final float SPEAKER_PREVIEW_BASE_SCALE = 72.0F;

    private final NpcDetailPayload npc;
    private final Dialogue dialogue;
    private final List<Button> responses = new ArrayList<>();
    private Moe preview;
    private int startTick;
    private int cursor;
    private boolean responseMode;
    private boolean closeSent;

    public DialogueScreen(DialogueOpenPayload payload) {
        super(Component.empty());
        this.npc = payload.npc();
        this.dialogue = payload.dialogue();
    }

    @Override
    protected void init() {
        this.startTick = this.minecraft.player == null ? 0 : this.minecraft.player.tickCount;
        this.cursor = this.dialogue.speaker().speaks() ? this.dialogue.text().length() : 0;
        this.responseMode = this.dialogue.tooltip();
        this.closeSent = false;
        this.preview = this.createPreview();
        this.rebuildResponseButtons();
        this.play(this.dialogue.speaker().speaks() ? this.dialogue.speaker().voice() : this.dialogue.sound());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        this.renderPreview(graphics, mouseX, mouseY);
        this.renderPanel(graphics, mouseX, mouseY, partialTick);
        this.renderWidgets(graphics, mouseX, mouseY, partialTick);
    }

    private void renderPanel(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int left = this.left();
        int top = this.bottom() - HEIGHT;
        graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, DIALOGUE_TEXTURE, left, top, 0.0F, 0.0F, WIDTH, HEIGHT, 256, 256);

        if (!this.responseMode || this.dialogue.tooltip()) {
            String visible = this.visibleText();
            List<net.minecraft.util.FormattedCharSequence> lines = this.font.split(Component.literal(visible), 232);
            for (int index = 0; index < Math.min(3, lines.size()); ++index) {
                graphics.drawString(this.font, lines.get(index), left + 5, top + 5 + index * 9, 0xFFFFFFFF, true);
            }
        }

        if (this.dialogue.speaker().identity() != Speaker.Identity.NARRATOR && this.npc.found()) {
            graphics.drawString(this.font, this.npc.name(), left + 5, top - 9, 0xFFFFFFFF, true);
        }

        if (this.cursor < this.dialogue.text().length() && this.minecraft.player != null) {
            this.cursor = Math.min(this.dialogue.text().length(), this.minecraft.player.tickCount - this.startTick);
        }
    }

    private void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        for (Renderable renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderPreview(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.dialogue.speaker().identity() == Speaker.Identity.NARRATOR || this.preview == null) {
            return;
        }
        int x = switch (this.dialogue.speaker().position()) {
            case LEFT -> this.width / 2 - 70;
            case CENTER -> this.width / 2;
            case RIGHT -> this.width / 2 + 70;
        };
        int y = this.bottom() - 50;
        int scale = Math.max(20, (int) (SPEAKER_PREVIEW_BASE_SCALE * this.dialogue.speaker().scale()));
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics,
                x - SPEAKER_PREVIEW_HALF_WIDTH,
                y - SPEAKER_PREVIEW_TOP_OFFSET,
                x + SPEAKER_PREVIEW_HALF_WIDTH,
                y + SPEAKER_PREVIEW_BOTTOM_OFFSET,
                scale,
                0.0F,
                mouseX,
                mouseY,
                this.preview);
        ++this.preview.tickCount;
    }

    private String visibleText() {
        return this.dialogue.text().substring(0, Math.min(this.cursor, this.dialogue.text().length()));
    }

    private void rebuildResponseButtons() {
        this.clearWidgets();
        this.responses.clear();
        int index = 0;
        for (Map.Entry<Response, String> entry : this.dialogue.responses().entrySet()) {
            ++index;
            Button button = this.dialogue.tooltip()
                    ? this.iconButton(index, entry.getKey(), entry.getValue())
                    : this.textButton(index, entry.getKey(), entry.getValue());
            this.responses.add(button);
            if (this.responseMode || this.dialogue.tooltip()) {
                this.addRenderableWidget(button);
            }
        }
        if (!this.dialogue.tooltip()) {
            this.addRenderableWidget(this.toggleButton());
        }
    }

    private Button toggleButton() {
        int iconX = this.responseMode ? 90 : 80;
        Component tooltip = Component.translatable("gui.block_party.button." + (this.responseMode ? "see_dialogue" : "see_response"));
        Button button = new SpriteButton(this.right() - 14, this.bottom() - 59, 10, 10, iconX, 84, tooltip, value -> {
            this.responseMode = !this.responseMode;
            this.rebuildResponseButtons();
        });
        button.setTooltip(Tooltip.create(tooltip));
        return button;
    }

    private Button iconButton(int index, Response response, String text) {
        Component label = Component.literal(text == null ? response.name() : text);
        Button button = new SpriteButton(this.left() + (index - 1) * 14 + 4, this.bottom() - 14, 10, 10, response.ordinal() * 10, 74, label, value -> this.respond(response));
        button.setTooltip(Tooltip.create(label));
        return button;
    }

    private Button textButton(int index, Response response, String text) {
        return new ResponseTextButton(this.left() + 4, this.bottom() - 44 + (index - 1) * 14, 234, 13,
                Component.literal(text == null ? response.name() : text), response, value -> this.respond(response));
    }

    private void respond(Response response) {
        PacketDistributor.sendToServer(new DialogueRespondPayload(this.npc.databaseId(), response));
        this.closeSent = true;
        this.minecraft.setScreen(null);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
            this.responseMode = false;
            this.rebuildResponseButtons();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
            this.responseMode = true;
            this.rebuildResponseButtons();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE) {
            this.cursor = this.dialogue.text().length();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.sendClose();
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        this.sendClose();
        super.onClose();
    }

    private void sendClose() {
        if (!this.closeSent) {
            PacketDistributor.sendToServer(new DialogueClosePayload(this.npc.databaseId()));
            this.closeSent = true;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private Moe createPreview() {
        if (this.minecraft == null || this.minecraft.level == null || !this.npc.found()) {
            return null;
        }
        Moe moe = CustomEntities.MOE.get().create(this.minecraft.level, EntitySpawnReason.TRIGGERED);
        if (moe == null) {
            return null;
        }
        BlockState state = Block.stateById(this.npc.blockStateId());
        moe.setDatabaseID(this.npc.databaseId());
        moe.setOwnerUUID(this.npc.ownerUuid());
        moe.setBlockStateFromRow(state);
        moe.setGivenName(this.npc.name());
        moe.setGender(this.npc.gender());
        moe.setEmotion(this.dialogue.speaker().emotion());
        moe.setAnimationKey(this.dialogue.speaker().animation());
        moe.setGuiPreview(true);
        return moe;
    }

    private void play(ResourceLocation soundId) {
        if (soundId == null || this.minecraft == null) {
            return;
        }
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(soundId);
        if (sound != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
        }
    }

    private int left() {
        return (this.width - WIDTH) / 2;
    }

    private int right() {
        return this.left() + WIDTH;
    }

    private int bottom() {
        return this.height - 24;
    }

    private static class SpriteButton extends Button {
        private final int textureX;
        private final int textureY;

        SpriteButton(int x, int y, int width, int height, int textureX, int textureY, Component message, OnPress onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
            this.textureX = textureX;
            this.textureY = textureY;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int y = this.textureY + (this.isHoveredOrFocused() ? 10 : 0);
            graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, DIALOGUE_TEXTURE, this.getX(), this.getY(), this.textureX, y, this.width, this.height, 256, 256);
        }
    }

    private static class ResponseTextButton extends Button {
        private final Response response;

        ResponseTextButton(int x, int y, int width, int height, Component message, Response response, OnPress onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
            this.response = response;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int y = this.isHoveredOrFocused() ? 61 : 48;
            graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, DIALOGUE_TEXTURE, this.getX(), this.getY(), 4.0F, y, this.width, this.height, 256, 256);
            graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, DIALOGUE_TEXTURE, this.getX() + 2, this.getY() + 1, this.response.ordinal() * 10, 74.0F, 10, 10, 256, 256);
            graphics.drawString(Minecraft.getInstance().font, this.getMessage(), this.getX() + 16, this.getY() + 2, 0xFFFFFFFF, true);
        }
    }
}
