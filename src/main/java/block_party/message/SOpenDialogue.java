package block_party.message;

import block_party.client.screen.DialogueScreen;
import block_party.convo.Dialogue;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SOpenDialogue extends AbstractMessage {
    private final Dialogue dialogue;

    public SOpenDialogue(FriendlyByteBuf buffer) {
        this(new Dialogue(buffer.readNbt()));
    }

    public SOpenDialogue(Dialogue dialogue) {
        super();
        this.dialogue = dialogue;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.dialogue.write(new CompoundTag()));
    }

    @Override
    public void handle(NetworkEvent.Context context, ServerPlayer player) {

    }

    @Override
    public void handle(NetworkEvent.Context context, Minecraft minecraft) {
        if (minecraft.screen != null) { minecraft.screen.onClose(); }
        minecraft.setScreen(new DialogueScreen(this.dialogue));
    }
}
