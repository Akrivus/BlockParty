package block_party.messages;

import block_party.npc.BlockPartyNPC;
import block_party.scene.dialogue.ResponseIcon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CDialogueRespond extends CNPCQuery {
    private final ResponseIcon response;

    public CDialogueRespond(long id, ResponseIcon icon) {
        super(id);
        this.response = icon;
    }

    public CDialogueRespond(FriendlyByteBuf buffer) {
        super(buffer);
        this.response = buffer.readEnum(ResponseIcon.class);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        super.encode(buffer);
        buffer.writeEnum(this.response);
    }

    @Override
    public void onFound(NetworkEvent.Context context, ServerPlayer player) {
        BlockPartyNPC npc = this.npc.getServerEntity(player.getServer());
        if (npc != null)
            npc.setResponse(this.response);
    }
}
