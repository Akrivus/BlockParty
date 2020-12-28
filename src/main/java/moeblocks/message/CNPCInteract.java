package moeblocks.message;

import moeblocks.datingsim.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CNPCInteract extends CNPCQuery {
    private final Response response;
    
    public CNPCInteract(UUID uuid, Response response) {
        super(uuid);
        this.response = response;
    }
    
    public CNPCInteract(PacketBuffer buffer) {
        super(buffer);
        this.response = buffer.readEnumValue(Response.class);
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
