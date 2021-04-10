package moeblocks.message;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CDialogueClose extends CNPCQuery {
    public CDialogueClose(UUID id) {
        super(id);
    }

    public CDialogueClose(PacketBuffer buffer) {
        super(buffer);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayerEntity player) {
        AbstractNPCEntity npc = this.npc.getServerEntity(player.getServer());
        if (npc != null) {
            //npc.setScene(Response.CLOSE);
        }
    }
}
