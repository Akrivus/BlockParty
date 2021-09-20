package block_party.client.screen;

import block_party.BlockParty;
import block_party.custom.CustomMessenger;
import block_party.client.animation.Animation;
import block_party.custom.CustomSounds;
import block_party.messages.CRemovePage;
import block_party.npc.BlockPartyNPC;
import block_party.utils.Trans;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class YearbookScreen extends ControllerScreen {
    public static final ResourceLocation YEARBOOK_TEXTURES = BlockParty.source("textures/gui/yearbook.png");
    private final String[] stats = new String[4];
    private final String[] lines = new String[4];
    private BlockPartyNPC entity;
    private String name;
    private String page;
    private Button buttonPreviousPage;
    private Button buttonNextPage;
    private Button buttonRemovePage;

    public YearbookScreen(List<Long> npcs, long id) {
        super(npcs, id, 146, 187);
        this.getNPC(id);
    }

    @Override
    public void setNPC() {
        this.entity = this.npc.getClientEntity(this.minecraft);
        this.entity.setAnimation(Animation.YEARBOOK.get());
        this.name = this.entity.getTypeName().getString();
        this.updateButtons();
        this.stats[0] = String.format("%.0f", this.entity.getHealth());
        this.stats[1] = String.format("%.0f", this.entity.getFullness());
        this.stats[2] = String.format("%.0f", this.entity.getLoyalty());
        this.stats[3] = String.format("%.0f", this.entity.getStress());
        this.lines[0] = Trans.late(this.entity.getDere().getTranslationKey());
        this.lines[1] = Trans.late(this.entity.getBloodType().getTranslationKey());
        this.lines[2] = String.format(Trans.late("characteristic.block_party.age"), this.entity.getAgeInYears());
        if (this.npc.isEstrangedFrom(this.getPlayer())) {
            this.lines[3] = Trans.late("characteristic.block_party.storyphase.estranged");
        } else if (this.npc.isDead()) {
            this.lines[3] = Trans.late("characteristic.block_party.storyphase.dead");
        } else {
            this.lines[3] = Trans.late("characteristic.block_party.storyphase.introduction");
        }
    }

    private void updateButtons() {
        if (this.npcs.isEmpty()) {
            this.getPlayer().displayClientMessage(new TranslatableComponent("gui.block_party.error.empty"), true);
            this.onClose();
        } else {
            this.page = String.format(Trans.late("gui.block_party.label.page"), this.index + 1, this.count);
            this.buttonNextPage.visible = this.index + 1 < this.count;
            this.buttonPreviousPage.visible = this.index - 1 >= 0;
            this.buttonRemovePage.visible = this.npc.isDeadOrEstrangedFrom(this.getPlayer());
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (this.npc == null) { return; }
        this.renderBackground(stack);
        this.renderPortrait(stack);
        this.renderEntity(this.getAbsoluteCenter(-3), 90, 40.0F, this.entity);
        if (this.npc.isDeadOrEstrangedFrom(this.getPlayer())) { this.renderOverlay(stack); }
        this.renderBook(stack);
        this.font.draw(stack, this.name, this.getCenter(this.font.width(this.name)) + 3, 91, 0);
        this.font.draw(stack, this.page, this.getCenter(this.font.width(this.page)), 185, 0);
        for (int x = 0; x < this.stats.length; ++x) {
            this.font.draw(stack, this.stats[x], this.getAbsoluteCenter(38) + x * 25, 104, 0);
        }
        for (int y = 0; y < this.lines.length; ++y) {
            this.font.draw(stack, this.lines[y], this.getAbsoluteCenter(40) - (y > 0 ? 5 : y), 123 + 10 * y, 0);
        }
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderTooltips(stack, mouseX, mouseY);
    }

    public void renderTooltips(PoseStack stack, int mouseX, int mouseY) {
        List<Component> text = new ArrayList<>();
        if (this.buttonRemovePage.isHovered()) {
            text.add(new TranslatableComponent("gui.block_party.button.remove"));
        }
        if (102 < mouseY && mouseY < 112) {
            if (this.getAbsoluteCenter(50) < mouseX && mouseX < this.getAbsoluteCenter(24)) {
                //text.add(new StringTextComponent(Trans.late(this.entity.getState(HealthState.class).getTranslationKey())));
            }
            if (this.getAbsoluteCenter(24) < mouseX && mouseX < this.getAbsoluteCenter(2)) {
                //text.add(new StringTextComponent(Trans.late(this.entity.getState(HungerState.class).getTranslationKey())));
            }
            if (this.getAbsoluteCenter(2) < mouseX && mouseX < this.getAbsoluteCenter(-24)) {
                //text.add(new StringTextComponent(Trans.late(this.entity.getState(LoveState.class).getTranslationKey())));
            }
            if (this.getAbsoluteCenter(-24) < mouseX && mouseX < this.getAbsoluteCenter(-50)) {
                //text.add(new StringTextComponent(Trans.late(this.entity.getState(StressState.class).getTranslationKey())));
            }
        }
        if (text.size() > 0) {
            this.renderTooltip(stack, Lists.transform(text, Component::getVisualOrderText), mouseX, mouseY);
        }
    }

    public void renderPortrait(PoseStack stack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, YEARBOOK_TEXTURES);
        this.blit(stack, this.getCenter(60) + 3, 27, 161, 25, 58, 58);
    }

    public void renderOverlay(PoseStack stack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, YEARBOOK_TEXTURES);
        if (this.npc.isDead()) { this.blit(stack, this.getCenter(60) + 2, 26, 160, 155, 60, 60); }
        if (this.npc.isEstrangedFrom(this.getPlayer())) {
            this.blit(stack, this.getCenter(60) + 2, 26, 160, 95, 60, 60);
        }
    }

    public void renderEntity(int posX, int posY, float scale, LivingEntity entity) {
        if (entity == null) { return; }
        PoseStack pose = RenderSystem.getModelViewStack();
        pose.pushPose();
        pose.translate(posX, posY, 1050.0);
        pose.scale(-1.0F, -1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack stack = new PoseStack();
        stack.translate(0.0D, 0.0D, 1000.0D);
        stack.scale(scale, scale, scale);
        stack.mulPose(new Quaternion(0.0F, -1.0F, 0.0F, 0.0F));
        Lighting.setupForEntityInInventory();
        EntityRenderDispatcher renderer = this.minecraft.getEntityRenderDispatcher();
        renderer.setRenderShadow(false);
        MultiBufferSource.BufferSource buffer = this.minecraft.renderBuffers().bufferSource();
        renderer.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, stack, buffer, 0xf000f0);
        buffer.endBatch();
        renderer.setRenderShadow(true);
        RenderSystem.applyModelViewMatrix();
        Lighting.setupFor3DItems();
    }

    public void renderBook(PoseStack stack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, YEARBOOK_TEXTURES);
        this.blit(stack, this.getCenter(146), 2, 0, 0, 146, 187);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) { return true; }
        switch (keyCode) {
        case GLFW.GLFW_KEY_RIGHT:
        case GLFW.GLFW_KEY_D:
            this.buttonNextPage.onPress();
            return true;
        case GLFW.GLFW_KEY_LEFT:
        case GLFW.GLFW_KEY_A:
            this.buttonPreviousPage.onPress();
            return true;
        default:
            return false;
        }
    }

    @Override
    protected void init() {
        this.addWidget(new Button(this.getCenter(136), 196, 136, 20, CommonComponents.GUI_DONE, (button) -> this.minecraft.setScreen(null)));
        this.buttonNextPage = this.addWidget(new TurnPageButton(this.getCenter(146) + 122, 51, 1, (button) -> {
            if (this.index + 1 < this.count) { this.getNPC(this.index + 1); }
        }));
        this.buttonPreviousPage = this.addWidget(new TurnPageButton(this.getCenter(146) + 21, 51, -1, (button) -> {
            if (this.index - 1 >= 0) { this.getNPC(this.index - 1); }
        }));
        this.buttonRemovePage = this.addWidget(new RemovePageButton(this.getCenter(146) + 115, 9, (button) -> {
            CustomMessenger.send(new CRemovePage(this.npc.getID()));
            this.npcs.remove(this.npc.getID());
            if (++this.index >= this.count) { --this.index; }
            if (this.index < 0) { this.index = 0; }
            if (this.npcs.isEmpty()) {
                this.onClose();
            } else {
                this.getNPC(this.index);
            }
        }));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public class TurnPageButton extends Button {
        private final int delta;

        public TurnPageButton(int x, int y, int delta, OnPress button) {
            super(x, y, 7, 10, TextComponent.EMPTY, button);
            this.delta = delta;
        }

        @Override
        public void playDownSound(SoundManager sound) {
            sound.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, YEARBOOK_TEXTURES);
            int x = this.delta > 0 ? 226 : 147;
            int y = this.isHovered() ? 35 : 63;
            this.blit(stack, this.x, this.y, x, y, 7, 10);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class RemovePageButton extends Button {
        public RemovePageButton(int x, int y, OnPress button) {
            super(x, y, 18, 18, TextComponent.EMPTY, button);
        }

        @Override
        public void playDownSound(SoundManager sound) {
            sound.play(SimpleSoundInstance.forUI(CustomSounds.ITEM_YEARBOOK_REMOVE_PAGE.get(), 1.0F));
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, YEARBOOK_TEXTURES);
            int x = this.isHovered() ? 164 : 146;
            this.blit(stack, this.x, this.y, x, 7, 18, 18);
        }
    }
}
