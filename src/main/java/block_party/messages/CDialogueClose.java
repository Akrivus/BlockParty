package block_party.messages;

import block_party.scene.dialogue.ResponseIcon;
import net.minecraft.network.FriendlyByteBuf;

public class CDialogueClose extends CDialogueRespond {
    public CDialogueClose(long id) {
        super(id, ResponseIcon.CLOSE_DIALOGUE);
    }

    public CDialogueClose(FriendlyByteBuf buffer) {
        super(buffer);
    }
}
