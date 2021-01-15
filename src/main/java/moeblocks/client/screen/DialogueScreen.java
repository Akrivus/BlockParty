package moeblocks.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.MoeMod;
import moeblocks.datingsim.convo.Dialogue;
import moeblocks.datingsim.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeEntities;
import moeblocks.init.MoeMessages;
import moeblocks.init.MoeSounds;
import moeblocks.message.CDialogueClose;
import moeblocks.message.CDialogueRespond;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class DialogueScreen extends AbstractScreen {
    public static final ResourceLocation DIALOGUE_TEXTURES = new ResourceLocation(MoeMod.ID, "textures/gui/dialogue.png");
    private final List<RespondButton> responses = new ArrayList<>();
    private final String[] lines = new String[] { "", "", "" };
    private final Dialogue dialogue;
    private AbstractNPCEntity entity;
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
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.setLines(this.line.substring(0, Math.min(this.cursor, this.line.length())), 0);
        this.renderDialogue(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        if (this.cursor < this.line.length()) {
            this.cursor = this.minecraft.player.ticksExisted - this.start;
            this.playSound(MoeSounds.ENTITY_MOE_SAY.get());
        }
        this.renderTooltips(stack, mouseX, mouseY);
    }
    
    public void renderTooltips(MatrixStack stack, int mouseX, int mouseY) {
        this.buttons.forEach((button) -> { if (button.isHovered()) { button.renderToolTip(stack, mouseX, mouseY); } });
    }
    
    public void renderDialogue(MatrixStack stack) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(DIALOGUE_TEXTURES);
        this.blit(stack, this.getLeft(), this.getBottom(48), this.zero, this.zero, this.sizeX, this.sizeY);
        this.font.drawStringWithShadow(stack, this.lines[0].trim(), this.getLeft(5), this.getBottom(43), this.white);
        this.font.drawStringWithShadow(stack, this.lines[1].trim(), this.getLeft(5), this.getBottom(34), this.white);
        this.font.drawStringWithShadow(stack, this.lines[2].trim(), this.getLeft(5), this.getBottom(25), this.white);
        this.font.drawStringWithShadow(stack, this.name, this.getLeft(5), this.getBottom(57), this.white);
    }

    public void playSound(SoundEvent sound) {
        this.minecraft.getSoundHandler().play(SimpleSound.master(sound, 1.0F));
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
            MoeMessages.send(new CDialogueClose(this.dialogue.getSpeaker().getUUID()));
        default:
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
    
    @Override
    protected void init() {
        this.entity = this.dialogue.getEntity(MoeEntities.MOE.get());
        this.name = this.entity.getFullName();
        this.line = this.dialogue.getLine();
        this.start = this.minecraft.player.ticksExisted;
        for (Response response : Response.values()) {
            if (this.dialogue.has(response)) { this.responses.add(this.addButton(new RespondButton(this, this.dialogue, response, this.role++))); }
        }
    }
    
    private void setLines(String words, int i) {
        this.lines[0] = this.lines[1] = this.lines[2] = "";
        for (String word : words.split(" ")) {
            String line = this.lines[i] + " " + word;
            if (this.font.getStringWidth(line) > 232) { ++i; line = word; }
            this.lines[i] = line;
        }
    }
    
    public class RespondButton extends Button {
        private final Response response;
        
        public RespondButton(DialogueScreen parent, Dialogue dialogue, Response response, int index) {
            super(parent.getLeft(index * 15 + 5), parent.getBottom(14), 10, 10, NarratorChatListener.EMPTY,
                  (button) -> MoeMessages.send(new CDialogueRespond(dialogue.getSpeaker().getUUID(), response)),
                  (button, stack, x, y) -> parent.renderTooltip(stack, new TranslationTextComponent(response.getKey()), x, y));
            this.response = response;
        }
        
        @Override
        public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindTexture(DIALOGUE_TEXTURES);
            this.blit(stack, this.x, this.y, this.response.ordinal() * 10, this.isHovered() ? 58 : 48, 10, 10);
        }
    }
}
