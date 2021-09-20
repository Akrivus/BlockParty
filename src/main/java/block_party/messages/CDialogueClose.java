package block_party.messages;

import block_party.npc.BlockPartyNPC;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class CDialogueClose extends CNPCQuery {
    public CDialogueClose(long id) {
        super(id);
    }

    public CDialogueClose(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayer player) {
        BlockPartyNPC npc = this.npc.getServerEntity(player.getServer());
        if (npc != null) {
            //npc.setScene(Response.CLOSE);
        }
    }
}
