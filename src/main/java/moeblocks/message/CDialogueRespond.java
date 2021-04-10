package moeblocks.message;

import moeblocks.convo.enums.Response;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class CDialogueRespond extends CNPCQuery {
    private final Response response;

    public CDialogueRespond(UUID id, Response response) {
        super(id);
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
    public void onFound(NetworkEvent.Context context, ServerPlayerEntity player) {
        AbstractNPCEntity npc = this.npc.getServerEntity(player.getServer());
        if (npc != null) {
            //npc.setScene(this.response);
        }
    }
}
