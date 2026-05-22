package block_party.messages;

import block_party.client.screens.DialogueScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SCloseDialogue extends AbstractMessage.Server {
    public SCloseDialogue() {
        super();
    }

    public SCloseDialogue(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) { }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {
        if (minecraft.screen instanceof DialogueScreen) { minecraft.setScreen(null); }
    }
}
