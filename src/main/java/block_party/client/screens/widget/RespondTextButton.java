package block_party.client.screens.widget;

import block_party.client.screens.DialogueScreen;
import block_party.client.screens.YearbookScreen;
import block_party.db.records.NPC;
import block_party.scene.Response;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class RespondTextButton extends DialogueScreen.RespondButton {
    public RespondTextButton(DialogueScreen parent, int index, NPC npc, Response icon, Component text) {
        super(parent.getLeft(4), parent.getBottom(44 - (index - 1) * 14), 234, 13, parent, npc, icon, text, false);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(YearbookScreen.YEARBOOK_TEXTURES, this.getX(), this.getY(), 4, this.isHoveredOrFocused() ? 61 : 48, 234, 13);
        this.renderIcon(graphics, this.getX() + 2, this.getY() + 1);
        graphics.drawString(this.font, this.text, this.getX() + 16, this.getY() + 2, 0xffffff, true);
    }
}
