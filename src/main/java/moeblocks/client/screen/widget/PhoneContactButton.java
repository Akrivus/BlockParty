package moeblocks.client.screen.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.client.screen.CellPhoneScreen;
import moeblocks.datingsim.CacheNPC;
import moeblocks.init.MoeMessages;
import moeblocks.init.MoeSounds;
import moeblocks.message.CPhoneTeleportMoe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhoneContactButton extends Button {
    private final CellPhoneScreen parent;
    private final CacheNPC npc;
    private final int index;
    
    public PhoneContactButton(CellPhoneScreen parent, CacheNPC npc, int index) {
        super(0, 0, 81, 15, StringTextComponent.EMPTY, (button) -> PhoneContactButton.act(parent, npc, index));
        this.parent = parent;
        this.npc = npc;
        this.index = index;
    }
    
    private static void act(CellPhoneScreen parent, CacheNPC npc, int index) {
        if (parent.isSelected(index)) {
            MoeMessages.send(new CPhoneTeleportMoe(npc.getUUID()));
            parent.closeScreen();
        } else {
            parent.setSelected(index);
        }
    }
    
    @Override
    public void renderButton(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(CellPhoneScreen.CELL_PHONE_TEXTURES);
        int y = this.isHovered() ? 98 : 115;
        this.blit(stack, this.x, this.y, 108, y, 81, 15);
        Minecraft.getInstance().fontRenderer.drawString(stack, this.npc.getName(), this.x + 10, this.y + 4, this.isHovered() ? 0xffffff : 0);
    }
    
    @Override
    public boolean isHovered() {
        return super.isHovered() || this.parent.isSelected(this.index);
    }
    
    @Override
    public void playDownSound(SoundHandler sound) {
        sound.play(SimpleSound.master(MoeSounds.CELL_PHONE_BUTTON.get(), 1.0F));
    }
}
