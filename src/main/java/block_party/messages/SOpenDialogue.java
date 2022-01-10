package block_party.messages;

import block_party.client.screens.DialogueScreen;
import block_party.db.records.NPC;
import block_party.scene.dialogue.ClientDialogue;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SOpenDialogue extends AbstractMessage.Server {
    private final NPC npc;
    private final ClientDialogue dialogue;

    public SOpenDialogue(FriendlyByteBuf buffer) {
        this(new NPC(buffer.readNbt()), new ClientDialogue(buffer.readNbt()));
    }

    public SOpenDialogue(NPC npc, ClientDialogue dialogue) {
        this.npc = npc;
        this.dialogue = dialogue;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.npc.write());
        buffer.writeNbt(this.dialogue.write());
    }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {
        if (minecraft.screen != null) { minecraft.screen.onClose(); }
        minecraft.setScreen(new DialogueScreen(this.dialogue, this.npc));
    }
}
