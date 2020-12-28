package moeblocks.message;

import moeblocks.client.screen.DialogueScreen;
import moeblocks.datingsim.convo.Dialogue;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SCloseDialogue extends AbstractMessage {
    public SCloseDialogue() {
        super();
    }
    
    public SCloseDialogue(PacketBuffer buffer) {
        super(buffer);
    }
    
    @Override
    public void encode(PacketBuffer buffer) { }
    
    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) { }
    
    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {
        if (minecraft.currentScreen instanceof DialogueScreen) { minecraft.displayGuiScreen(null); }
    }
}
