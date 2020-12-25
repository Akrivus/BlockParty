package moeblocks.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.client.screen.YearbookScreen;
import moeblocks.init.MoeSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RemovePageButton extends Button {
    public RemovePageButton(int x, int y, IPressable button) {
        super(x, y, 18, 18, StringTextComponent.EMPTY, button);
    }
    
    @Override
    public void playDownSound(SoundHandler sound) {
        sound.play(SimpleSound.master(MoeSounds.YEARBOOK_REMOVE_PAGE.get(), 1.0F));
    }
    
    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(YearbookScreen.YEARBOOK_TEXTURES);
        int x = this.isHovered() ? 164 : 146;
        this.blit(stack, this.x, this.y, x, 7, 18, 18);
    }
}
