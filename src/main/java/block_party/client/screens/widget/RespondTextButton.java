package block_party.client.screens.widget;

import block_party.client.screens.DialogueScreen;
import block_party.db.records.NPC;
import block_party.scene.Response;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TextComponent;

public class RespondTextButton extends DialogueScreen.RespondButton {
    public RespondTextButton(DialogueScreen parent, int index, NPC npc, Response icon, TextComponent text) {
        super(parent.getLeft(4), parent.getBottom(44 - (index - 1) * 14), 234, 13, parent, npc, icon, text, false);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        this.blit(stack, this.x, this.y, 4, this.isHoveredOrFocused() ? 61 : 48, 234, 13);
        this.renderIcon(stack, this.x + 2, this.y + 1);
        this.font.drawShadow(stack, this.text, this.x + 16, this.y + 2, 0xffffff);
    }
}
