package moeblocks.message;

import moeblocks.datingsim.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CDialogueRespond extends CNPCQuery {
    private final Response response;
    
    public CDialogueRespond(UUID uuid, Response response) {
        super(uuid);
        this.response = response;
    }
    
    public CDialogueRespond(PacketBuffer buffer) {
        super(buffer);
        this.response = buffer.readEnumValue(Response.class);
    }
    
    @Override
    public void encode(PacketBuffer buffer) {
        super.encode(buffer);
        buffer.writeEnumValue(this.response);
    }
    
    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) {
        super.handle(context, player);
        if (this.npc == null) { return; }
        AbstractNPCEntity npc = this.npc.get(player.getServer());
        if (npc != null) {
            npc.setScene(this.response);
        }
    }
}
