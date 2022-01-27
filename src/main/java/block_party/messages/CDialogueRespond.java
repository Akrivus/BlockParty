package block_party.messages;

import block_party.entities.BlockPartyNPC;
import block_party.scene.Response;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CDialogueRespond extends CNPCQuery {
    private final Response.Icon response;

    public CDialogueRespond(long id, Response.Icon icon) {
        super(id);
        this.response = icon;
    }

    public CDialogueRespond(FriendlyByteBuf buffer) {
        super(buffer);
        this.response = buffer.readEnum(Response.Icon.class);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeEnum(this.response);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayer player) {
        BlockPartyNPC npc = this.npc.getServerEntity(player.getServer());
        if (npc != null) { npc.setResponse(this.response); }
    }
}
