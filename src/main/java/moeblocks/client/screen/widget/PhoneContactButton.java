package moeblocks.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.client.screen.CellPhoneScreen;
import moeblocks.datingsim.CacheNPC;
import moeblocks.init.MoeMessages;
import moeblocks.init.MoeSounds;
import moeblocks.message.CNPCTeleport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhoneContactButton extends Button {
    public PhoneContactButton(CellPhoneScreen parent, CacheNPC npc) {
        super(0, 0, 81, 15, new StringTextComponent(npc.getName()), (button) -> {
            MoeMessages.send(new CNPCTeleport(npc.getUUID()));
            parent.closeScreen();
        });
    }
    
    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int color = this.isHovered() ? 0xffffff : 0xff7fb6;
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer font = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(CellPhoneScreen.CELL_PHONE_TEXTURES);
        this.blit(stack, this.x, this.y, 108, this.isHovered() ? 98 : 115, 81, 15);
        font.drawString(stack, this.getMessage().getString(), this.x + 10, this.y + 4, color);
    }
    
    @Override
    public void playDownSound(SoundHandler sound) {
        sound.play(SimpleSound.master(MoeSounds.CELL_PHONE_BUTTON.get(), 1.0F));
    }
}
