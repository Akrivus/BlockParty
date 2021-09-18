package block_party.message;

import block_party.init.BlockPartyMessages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class CNPCRequest extends CNPCQuery {
    public CNPCRequest(long id) {
        super(id);
    }

    public CNPCRequest(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayer player) {
        BlockPartyMessages.send(player, new SNPCResponse(this.npc));
    }
}
