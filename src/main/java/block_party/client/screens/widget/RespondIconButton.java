package block_party.client.screens.widget;

import block_party.client.screens.DialogueScreen;
import block_party.db.records.NPC;
import block_party.scene.Response;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.TextComponent;

public class RespondIconButton extends DialogueScreen.RespondButton {
    public RespondIconButton(DialogueScreen parent, int index, NPC npc, Response.Icon icon, TextComponent text) {
        super(parent.getLeft((index - 1) * 10 + index * 4), parent.getBottom(14), 10, 10, parent, npc, icon, text, true);
    }

    @Override
    public void renderButton(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.renderButton(stack, mouseX, mouseY, partialTicks);
        this.renderIcon(stack, this.x, this.y);
    }
}
