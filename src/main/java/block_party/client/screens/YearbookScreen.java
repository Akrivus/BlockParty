package block_party.client.screens;

import block_party.BlockParty;
import block_party.client.ClientTranslations;
import block_party.entities.Moe;
import block_party.network.payload.NpcDetailPayload;
import block_party.network.payload.NpcRemoveRequestPayload;
import block_party.registry.CustomEntities;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class YearbookScreen extends ControllerScreen {
    public static final ResourceLocation YEARBOOK_TEXTURE = BlockParty.source("textures/gui/yearbook.png");
    private static final int WIDTH = 146;
    private static final int HEIGHT = 187;

    private NpcDetailPayload npc;
    private Moe preview;
    private Button previousButton;
    private Button nextButton;
    private Button removeButton;

    public YearbookScreen(List<NpcDetailPayload> npcs, long selectedDatabaseId) {
        super(npcs, selectedDatabaseId);
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(this.left(136), 196, 136, 20)
                .build());
        this.previousButton = this.addRenderableWidget(new PageButton(this.left(WIDTH) + 21, 51, false, button -> this.previousPage()));
        this.nextButton = this.addRenderableWidget(new PageButton(this.left(WIDTH) + 122, 51, true, button -> this.nextPage()));
        Component remove = Component.translatable("gui.block_party.button.remove");
        this.removeButton = this.addRenderableWidget(new RemovePageButton(this.left(WIDTH) + 115, 9, button -> this.removeSelected()));
        this.removeButton.setTooltip(Tooltip.create(remove));
        this.showSelected();
        this.updateButtons();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        this.renderBook(graphics);
        if (this.npc != null && this.npc.found()) {
            this.renderPreview(graphics, mouseX, mouseY);
            this.renderDetails(graphics);
        }
        this.renderWidgets(graphics, mouseX, mouseY, partialTick);
    }

    private void renderBook(GuiGraphics graphics) {
        graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, YEARBOOK_TEXTURE, this.left(WIDTH), 2, 0.0F, 0.0F, WIDTH, HEIGHT, 256, 256);
    }

    private void renderPreview(GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.preview == null) {
            return;
        }
        InventoryScreen.renderEntityInInventoryFollowsMouse(graphics, this.width / 2 - 33, 28, this.width / 2 + 27, 88, 34, 0.0F, mouseX, mouseY, this.preview);
        ++this.preview.tickCount;
    }

    private void renderDetails(GuiGraphics graphics) {
        String name = ClientTranslations.displayName(this.npc).getString();
        graphics.drawString(this.font, name, this.width / 2 - this.font.width(name) / 2 + 3, 91, 0, false);
        String page = this.npcs.isEmpty()
                ? ClientTranslations.page(0, 0).getString()
                : ClientTranslations.page(this.index + 1, this.npcs.size()).getString();
        graphics.drawString(this.font, page, this.width / 2 - this.font.width(page) / 2, 185, 0, false);
        String[] stats = {
                String.format("%.0f", this.npc.health()),
                String.format("%.0f", this.npc.foodLevel()),
                String.format("%.0f", this.npc.loyalty()),
                String.format("%.0f", this.npc.stress())
        };
        for (int x = 0; x < stats.length; ++x) {
            graphics.drawString(this.font, stats[x], this.absoluteCenter(38) + x * 25, 104, 0, false);
        }
        Component[] lines = {
                ClientTranslations.dere(this.npc.dere()),
                ClientTranslations.bloodType(this.npc.bloodType()),
                ClientTranslations.zodiac(this.npc.zodiac()),
                ClientTranslations.relationship(this.npc, this.minecraft.player == null ? null : this.minecraft.player.getUUID())
        };
        for (int y = 0; y < lines.length; ++y) {
            graphics.drawString(this.font, lines[y], this.absoluteCenter(40) - (y > 0 ? 5 : y), 123 + 10 * y, 0, false);
        }
    }

    private void showSelected() {
        NpcDetailPayload selected = this.selectedNpc();
        if (selected == null) {
            this.onClose();
            return;
        }
        this.npc = selected;
        this.preview = this.createPreview(selected);
    }

    private void nextPage() {
        if (this.index + 1 < this.npcs.size()) {
            ++this.index;
            this.showSelected();
            this.updateButtons();
            this.playPageTurn();
        }
    }

    private void previousPage() {
        if (this.index > 0) {
            --this.index;
            this.showSelected();
            this.updateButtons();
            this.playPageTurn();
        }
    }

    private void removeSelected() {
        if (!this.canRemoveSelected()) {
            return;
        }
        Long selected = this.selectedId();
        if (selected == null) {
            this.onClose();
            return;
        }
        PacketDistributor.sendToServer(new NpcRemoveRequestPayload(selected));
        this.npcs.remove(this.index);
        if (this.index >= this.npcs.size()) {
            --this.index;
        }
        if (this.index < 0) {
            this.index = 0;
        }
        if (this.npcs.isEmpty()) {
            this.onClose();
        } else {
            this.showSelected();
        }
        this.updateButtons();
    }

    private void updateButtons() {
        if (this.previousButton != null) {
            this.previousButton.visible = this.index > 0;
        }
        if (this.nextButton != null) {
            this.nextButton.visible = this.index + 1 < this.npcs.size();
        }
        if (this.removeButton != null) {
            this.removeButton.visible = this.canRemoveSelected();
        }
    }

    private boolean canRemoveSelected() {
        if (this.npc == null || !this.npc.found() || this.minecraft == null || this.minecraft.player == null) {
            return false;
        }
        return true;
    }

    private void playPageTurn() {
        if (this.minecraft != null) {
            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }

    private Moe createPreview(NpcDetailPayload payload) {
        if (this.minecraft == null || this.minecraft.level == null || !payload.found()) {
            return null;
        }
        Moe moe = CustomEntities.MOE.get().create(this.minecraft.level, EntitySpawnReason.TRIGGERED);
        if (moe == null) {
            return null;
        }
        moe.setDatabaseID(payload.databaseId());
        moe.setPlayerUUID(payload.playerUuid());
        moe.setBlockStateFromRow(Block.stateById(payload.blockStateId()));
        moe.setGivenName(payload.name());
        moe.setGender(payload.gender());
        moe.setBloodType(payload.bloodType());
        moe.setDere(payload.dere());
        moe.setZodiac(payload.zodiac());
        moe.setFoodLevel(payload.foodLevel());
        moe.setLoyalty(payload.loyalty());
        moe.setStress(payload.stress());
        moe.setAnimationKey("YEARBOOK");
        moe.setGuiPreview(true);
        return moe;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_D) {
            this.nextPage();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_A) {
            this.previousPage();
            return true;
        }
        return false;
    }

    private static class PageButton extends Button {
        private final boolean next;

        PageButton(int x, int y, boolean next, OnPress onPress) {
            super(x, y, 7, 10, Component.empty(), onPress, DEFAULT_NARRATION);
            this.next = next;
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int textureX = this.next ? 226 : 147;
            int textureY = this.isHoveredOrFocused() ? 35 : 63;
            graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, YEARBOOK_TEXTURE, this.getX(), this.getY(), textureX, textureY, this.width, this.height, 256, 256);
        }
    }

    private static class RemovePageButton extends Button {
        RemovePageButton(int x, int y, OnPress onPress) {
            super(x, y, 18, 18, Component.empty(), onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, YEARBOOK_TEXTURE, this.getX(), this.getY(), this.isHoveredOrFocused() ? 164 : 146, 7, this.width, this.height, 256, 256);
        }
    }
}
