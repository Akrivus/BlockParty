package moeblocks.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.client.screen.YearbookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChangePageButton extends Button {
    private final int delta;

    public ChangePageButton(int x, int y, int delta, IPressable button) {
        super(x, y, 7, 10, StringTextComponent.EMPTY, button);
        this.delta = delta;
    }

    @Override
    public void playDownSound(SoundHandler sound) {
        sound.play(SimpleSound.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
    }

    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(YearbookScreen.YEARBOOK_TEXTURES);
        int x = this.delta > 0 ? 226 : 147;
        int y = this.isHovered() ? 35 : 63;
        this.blit(stack, this.x, this.y, x, y, 7, 10);
    }
}
