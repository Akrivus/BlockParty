package block_party.messages;

import block_party.scene.Response;
import net.minecraft.network.FriendlyByteBuf;

public class CDialogueClose extends CDialogueRespond {
    public CDialogueClose(long id) {
        super(id, Response.Icon.CLOSE_DIALOGUE);
    }

    public CDialogueClose(FriendlyByteBuf buffer) {
        super(buffer);
    }
}
