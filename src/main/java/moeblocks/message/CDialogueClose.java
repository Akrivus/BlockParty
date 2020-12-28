package moeblocks.message;

import moeblocks.datingsim.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CDialogueClose extends CNPCQuery {
    public CDialogueClose(UUID uuid) {
        super(uuid);
    }
    
    public CDialogueClose(PacketBuffer buffer) {
        super(buffer);
    }
    
    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) {
        super.handle(context, player);
        if (this.npc == null) { return; }
        AbstractNPCEntity npc = this.npc.get(player.getServer());
        if (npc != null) {
            npc.setScene(Response.CLOSE);
        }
    }
}
