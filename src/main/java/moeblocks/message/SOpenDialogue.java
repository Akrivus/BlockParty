package moeblocks.message;

import moeblocks.client.screen.CellPhoneScreen;
import moeblocks.client.screen.ControllerScreen;
import moeblocks.client.screen.DialogueScreen;
import moeblocks.datingsim.convo.Dialogue;
import moeblocks.init.MoeItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.UUID;

public class SOpenDialogue extends AbstractMessage {
    private final Dialogue dialogue;
    
    public SOpenDialogue(Dialogue dialogue) {
        super();
        this.dialogue = dialogue;
    }
    
    public SOpenDialogue(PacketBuffer buffer) {
        this(new Dialogue(buffer.readCompoundTag()));
    }
    
    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeCompoundTag(this.dialogue.write(new CompoundNBT()));
    }
    
    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) {
    
    }
    
    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {
        minecraft.displayGuiScreen(new DialogueScreen(this.dialogue));
    }
}
