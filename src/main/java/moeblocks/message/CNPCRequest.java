package moeblocks.message;

import moeblocks.init.MoeMessages;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CNPCRequest extends CNPCQuery {
    public CNPCRequest(UUID uuid) {
        super(uuid);
    }
    
    public CNPCRequest(PacketBuffer buffer) {
        super(buffer);
    }
    
    @Override
    public void handle(NetworkEvent.Context context, ServerPlayerEntity player) {
        super.handle(context, player);
        if (this.npc == null) { return; }
        MoeMessages.send(player, new SNPCResponse(this.npc));
    }
}
