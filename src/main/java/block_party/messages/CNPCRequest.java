package block_party.messages;

import block_party.custom.CustomMessenger;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CNPCRequest extends CNPCQuery {
    public CNPCRequest(long id) {
        super(id);
    }

    public CNPCRequest(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayer player) {
        CustomMessenger.send(player, new SNPCResponse(this.npc));
    }
}
