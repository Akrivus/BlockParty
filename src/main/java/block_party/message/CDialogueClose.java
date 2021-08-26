package block_party.message;

import block_party.mob.Partyer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;

public class CDialogueClose extends CNPCQuery {
    public CDialogueClose(UUID id) {
        super(id);
    }

    public CDialogueClose(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayer player) {
        Partyer npc = this.npc.getServerEntity(player.getServer());
        if (npc != null) {
            //npc.setScene(Response.CLOSE);
        }
    }
}
