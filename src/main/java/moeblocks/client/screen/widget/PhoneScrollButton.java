package moeblocks.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.client.screen.CellPhoneScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhoneScrollButton extends Button {
    public PhoneScrollButton(CellPhoneScreen parent, int x, int y, int delta) {
        super(x, y, 7, 7, StringTextComponent.EMPTY, (button) -> parent.setScroll(delta, true));
    }

    @Override
    public void playDownSound(SoundHandler sound) { }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(CellPhoneScreen.CELL_PHONE_TEXTURES);
        int x = this.isHovered() ? 116 : 108;
        this.blit(stack, this.x, this.y, x, 73, 7, 7);
    }
}
