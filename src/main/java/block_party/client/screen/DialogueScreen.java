package block_party.client.screen;

import block_party.BlockParty;
import block_party.convo.Dialogue;
import block_party.convo.enums.Response;
import block_party.custom.CustomMessenger;
import block_party.custom.CustomSounds;
import block_party.messages.CDialogueClose;
import block_party.messages.CDialogueRespond;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class DialogueScreen extends AbstractScreen {
    public static final ResourceLocation DIALOGUE_TEXTURES = BlockParty.source("textures/gui/dialogue.png");
    private final List<RespondButton> responses = new ArrayList<>();
    private final String[] lines = new String[] { "", "", "" };
    private final Dialogue dialogue;
    private String name;
    private String line;
    private int start;
    private int cursor;
    private int role;

    public DialogueScreen(Dialogue dialogue) {
        super(242, 48);
        this.dialogue = dialogue;
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        this.setLines(this.line.substring(0, Math.min(this.cursor, this.line.length())), 0);
        this.renderDialogue(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.cursor < this.line.length()) {
            this.cursor = this.minecraft.player.tickCount - this.start;
            this.playSound(CustomSounds.NPC_SAY.get());
        }
        this.renderTooltips(stack, mouseX, mouseY);
    }

    public void renderTooltips(PoseStack stack, int mouseX, int mouseY) {
        this.children().forEach((child) -> {
            if (!(child instanceof Button)) { return; }
            Button button = (Button) child;
            if (button.isMouseOver(mouseX, mouseY)) { button.renderToolTip(stack, mouseX, mouseY); }
        });
    }

    public void renderDialogue(PoseStack stack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, DIALOGUE_TEXTURES);
        this.blit(stack, this.getLeft(), this.getBottom(48), this.zero, this.zero, this.sizeX, this.sizeY);
        this.font.drawShadow(stack, this.lines[0].trim(), this.getLeft(5), this.getBottom(43), this.white);
        this.font.drawShadow(stack, this.lines[1].trim(), this.getLeft(5), this.getBottom(34), this.white);
        this.font.drawShadow(stack, this.lines[2].trim(), this.getLeft(5), this.getBottom(25), this.white);
        this.font.drawShadow(stack, this.name, this.getLeft(5), this.getBottom(57), this.white);
    }

    public void playSound(SoundEvent sound) {
        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound, 1.0F));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
        case GLFW.GLFW_KEY_1:
        case GLFW.GLFW_KEY_2:
        case GLFW.GLFW_KEY_3:
        case GLFW.GLFW_KEY_4:
        case GLFW.GLFW_KEY_5:
        case GLFW.GLFW_KEY_6:
        case GLFW.GLFW_KEY_7:
        case GLFW.GLFW_KEY_8:
        case GLFW.GLFW_KEY_9:
            if (this.responses.size() >= keyCode - 48) {
                this.responses.get(keyCode - 49).onPress();
                return true;
            }
        case GLFW.GLFW_KEY_0:
            if (this.responses.size() >= 10) {
                this.responses.get(9).onPress();
                return true;
            }
            return false;
        case GLFW.GLFW_KEY_ENTER:
        case GLFW.GLFW_KEY_SPACE:
            this.cursor = this.line.length();
            return true;
        case GLFW.GLFW_KEY_ESCAPE:
            CustomMessenger.send(new CDialogueClose(this.dialogue.getSpeaker().getID()));
        default:
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    protected void init() {
        this.name = this.dialogue.getSpeaker().getName();
        this.line = this.dialogue.getLine();
        this.start = this.minecraft.player.tickCount;
        for (Response response : Response.values()) {
            if (this.dialogue.has(response)) {
                this.responses.add(this.addWidget(new RespondButton(this, this.dialogue, response, this.role++)));
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
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

    public class RespondButton extends Button {
        private final Response response;

        public RespondButton(DialogueScreen parent, Dialogue dialogue, Response response, int index) {
            super(parent.getLeft(index * 15 + 5), parent.getBottom(14), 10, 10, NarratorChatListener.NO_TITLE, (button) -> CustomMessenger.send(new CDialogueRespond(dialogue.getSpeaker().getID(), response)), (button, stack, x, y) -> parent.renderTooltip(stack, new TranslatableComponent(response.getKey()), x, y));
            this.response = response;
        }

        @Override
        public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, DIALOGUE_TEXTURES);
            this.blit(stack, this.x, this.y, this.response.ordinal() * 10, this.isHovered ? 58 : 48, 10, 10);
        }
    }
}
