package moeblocks.message;

import moeblocks.init.MoeMessages;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CNPCRequest extends CNPCQuery {
    public CNPCRequest(UUID id) {
        super(id);
    }

    public CNPCRequest(PacketBuffer buffer) {
        super(buffer);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayerEntity player) {
        MoeMessages.send(player, new SNPCResponse(this.npc));
    }
}
