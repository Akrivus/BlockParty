package block_party.client.screens;

import block_party.BlockParty;
import block_party.client.screens.widget.RespondIconButton;
import block_party.client.screens.widget.RespondTextButton;
import block_party.db.records.NPC;
import block_party.entities.BlockPartyNPC;
import block_party.messages.CDialogueClose;
import block_party.messages.CDialogueRespond;
import block_party.registry.CustomMessenger;
import block_party.scene.Dialogue;
import block_party.scene.Response;
import block_party.scene.Speaker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class DialogueScreen extends AbstractScreen {
    public static final ResourceLocation DIALOGUE_TEXTURES = BlockParty.source("textures/gui/dialogue.png");
    private final List<RespondButton> responses = new ArrayList<>();
    private final String[] lines = new String[] { "", "", "" };
    private final Dialogue dialogue;
    private final Speaker speaker;
    private final NPC npc;
    private BlockPartyNPC entity;
    private String name;
    private String text;
    private int start;
    private int cursor;
    private int role;
    private boolean onDialogueScreen = true;
    private Button buttonSeeResponse;
    private Button buttonSeeDialogue;

    public DialogueScreen(Dialogue dialogue, NPC npc) {
        super(242, 48);
        this.dialogue = dialogue;
        this.speaker = dialogue.getSpeaker();
        this.npc = npc;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        this.setLines(this.text.substring(0, Math.min(this.cursor, this.text.length())), 0);
        this.renderEntity(this.getAbsoluteCenter(64), 200, 40.0F, this.entity);
        this.renderDialogue(stack, mouseX, mouseY, partialTicks);
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.cursor < this.text.length()) {
            this.cursor = this.minecraft.player.tickCount - this.start;
            this.playSound(this.dialogue.getSound());
        }
        this.renderTooltips(stack, mouseX, mouseY);
    }

    public void renderDialogue(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, DIALOGUE_TEXTURES);
        this.blit(stack, this.getLeft(), this.getBottom(48), this.zero, this.zero, this.sizeX, this.sizeY);
        if (this.onDialogueScreen || this.dialogue.isTooltip()) {
            this.font.drawShadow(stack, this.lines[0].trim(), this.getLeft(5), this.getBottom(43), this.white);
            this.font.drawShadow(stack, this.lines[1].trim(), this.getLeft(5), this.getBottom(34), this.white);
            this.font.drawShadow(stack, this.lines[2].trim(), this.getLeft(5), this.getBottom(25), this.white);
        }
        if (this.speaker.identity == Speaker.Identity.NARRATOR) { return; }
        this.font.drawShadow(stack, this.name, this.getLeft(5), this.getBottom(57), this.white);
    }

    @Override
    protected void renderEntity(int posX, int posY, float scale, LivingEntity entity) {
        if (this.speaker.identity == Speaker.Identity.NARRATOR) { return; }
        super.renderEntity(posX, posY, scale, entity);
        ++entity.tickCount;
    }

    @Override
    protected void setEntityViewStack(PoseStack pose, int posX, int posY) {
        pose.translate(posX, posY, 2050.0);
        pose.scale(3.0F, -3.0F, -3.0F);
    }

    @Override
    protected void setEntityModelStack(PoseStack pose, float scale) {
        scale *= this.speaker.scale;
        switch (this.speaker.position) {
        case LEFT:
            pose.translate( 0.0D, 0.0D, 1000.0D);
            pose.scale(scale, scale, scale);
            pose.mulPose(new Quaternionf(0.0F, 1.0F, 0.0F,  0.2F));
            return;
        case CENTER:
            pose.translate(25.0D, 0.0D, 1000.0D);
            pose.scale(scale, scale, scale);
            pose.mulPose(new Quaternionf(0.0F, 1.0F, 0.0F,  0.0F));
            return;
        case RIGHT:
            pose.translate(50.0D, 0.0D, 1000.0D);
            pose.scale(scale, scale, scale);
            pose.mulPose(new Quaternionf(0.0F, 1.0F, 0.0F, -0.2F));
            return;
        }
    }

    public void renderTooltips(PoseStack stack, int mouseX, int mouseY) {
        this.children().forEach((child) -> {
            if (!(child instanceof Button)) { return; }
            Button button = (Button) child;
            if (button.isHoveredOrFocused()) {
                //button.setTooltip(stack, mouseX, mouseY); ???
            }
        });
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
        case GLFW.GLFW_KEY_LEFT:
        case GLFW.GLFW_KEY_A:
            this.updateButtons(true);
            return true;
        case GLFW.GLFW_KEY_RIGHT:
        case GLFW.GLFW_KEY_D:
            this.updateButtons(false);
            return true;
        case GLFW.GLFW_KEY_ENTER:
        case GLFW.GLFW_KEY_SPACE:
            this.cursor = this.text.length();
            return true;
        case GLFW.GLFW_KEY_ESCAPE:
            CustomMessenger.send(new CDialogueClose(this.npc.getID()));
        default:
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    protected void init() {
        this.start = this.minecraft.player.tickCount;
        this.text = this.dialogue.getText();
        this.name = this.npc.getName();
        this.entity = this.npc.getClientEntity(this.minecraft);
        this.speaker.stage(this.entity);
        this.buttonSeeResponse = new SeeButton(this, false);
        this.buttonSeeDialogue = new SeeButton(this, true);
        this.dialogue.getResponses().forEach((icon, text) -> {
            this.responses.add(RespondButton.create(this, ++this.role, this.npc, icon, Component.literal(text), this.dialogue.isTooltip()));
        });
        updateButtons(true);
        if (this.speaker.speaks) {
            this.playSound(this.speaker.voice);
            this.cursor = this.text.length();
        }
    }

    public void playSound(SoundEvent sound) {
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void updateButtons(boolean toggle) {
        this.onDialogueScreen = toggle;
        if (this.dialogue.isTooltip()) {
            this.responses.forEach((button) -> this.addRenderableWidget(button));
            this.removeWidget(this.buttonSeeDialogue);
            this.removeWidget(this.buttonSeeResponse);
        } else if (this.onDialogueScreen) {
            this.responses.forEach((button) -> this.removeWidget(button));
            this.removeWidget(this.buttonSeeDialogue);
            this.addRenderableWidget(this.buttonSeeResponse);
        } else {
            this.responses.forEach((button) -> this.addRenderableWidget(button));
            this.addRenderableWidget(this.buttonSeeDialogue);
            this.removeWidget(this.buttonSeeResponse);
        }
    }

    private void setLines(String words, int i) {
        this.lines[0] = this.lines[1] = this.lines[2] = "";
        for (String word : words.split(" ")) {
            String line = this.lines[i] + " " + word;
            if (this.font.width(line) > 232) {
                ++i;
                line = word;
            }
            this.lines[i] = line;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public abstract static class RespondButton extends Button {
        protected final Response icon;
        protected final String text;
        protected final Font font;

        public RespondButton(int posX, int posY, int sizeX, int sizeY, DialogueScreen parent, NPC npc, Response icon, Component text, boolean tooltip) {
            super(posX, posY, sizeX, sizeY, text, (button) -> RespondButton.act(parent, npc, icon), (n) -> text.copy());
            this.icon = icon;
            this.text = text.getString();
            this.font = parent.font;
        }

        private static void act(DialogueScreen parent, NPC npc, Response icon) {
            CustomMessenger.send(new CDialogueRespond(npc.getID(), icon));
            parent.onClose();
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, DialogueScreen.DIALOGUE_TEXTURES);
        }

        protected void renderIcon(PoseStack stack, int posX, int posY) {
            this.blit(stack, posX, posY, this.icon.ordinal() * 10, this.isHoveredOrFocused() ? 84 : 74, 10, 10);
        }

        public static RespondButton create(DialogueScreen parent, int index, NPC npc, Response icon, Component text, boolean tooltip) {
            if (tooltip) {
                return new RespondIconButton(parent, index, npc, icon, text);
            } else {
                return new RespondTextButton(parent, index, npc, icon, text);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public class SeeButton extends Button {
        private final boolean toggle;
        private int offset;
        private float ticks;
        private boolean backwards;

        public SeeButton(DialogueScreen parent, boolean toggle) {
            super(parent.getRight(14), parent.getBottom(59), 10, 10, Component.empty(), (button) -> parent.updateButtons(toggle), (n) -> Component.translatable(String.format("gui.block_party.button.%s", toggle ? "see_dialogue" : "see_response")));
            this.toggle = toggle;
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, DIALOGUE_TEXTURES);
            int x = this.toggle ? 90 : 80;
            int y = this.isHoveredOrFocused() ? 84 : 74;
            this.blit(stack, this.getX() + this.offset, this.getY(), x, y, 10, 10);
            this.ticks += this.backwards ? -partialTicks : partialTicks;
            if (this.ticks > 20.0F) {
                this.ticks = 20.0F;
                this.offset = this.toggle ? -1 : 1;
                this.backwards = true;
            } else if (this.ticks < 0.0F) {
                this.ticks = 0.0F;
                this.offset = 0;
                this.backwards = false;
            }
        }
    }
}
