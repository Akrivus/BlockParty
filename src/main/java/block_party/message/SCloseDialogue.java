package block_party.message;

import block_party.client.screen.DialogueScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SCloseDialogue extends AbstractMessage {
    public SCloseDialogue() {
        super();
    }

    public SCloseDialogue(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) { }

    @Override
    public void handle(NetworkEvent.Context context, ServerPlayer player) { }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {
        if (minecraft.screen instanceof DialogueScreen) { minecraft.setScreen(null); }
    }
}
