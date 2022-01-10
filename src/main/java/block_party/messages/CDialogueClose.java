package block_party.messages;

import block_party.npc.BlockPartyNPC;
import block_party.scene.dialogue.ResponseIcon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class CDialogueClose extends CDialogueRespond {
    public CDialogueClose(long id) {
        super(id, ResponseIcon.CLOSE_DIALOGUE);
    }

    public CDialogueClose(FriendlyByteBuf buffer) {
        super(buffer);
    }
}
