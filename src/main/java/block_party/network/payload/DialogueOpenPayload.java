package block_party.network.payload;

import block_party.BlockParty;
import block_party.scene.Dialogue;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DialogueOpenPayload(NpcDetailPayload npc, Dialogue dialogue) implements CustomPacketPayload {
    public static final Type<DialogueOpenPayload> TYPE = new Type<>(BlockParty.source("dialogue_open"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueOpenPayload> STREAM_CODEC =
            CustomPacketPayload.codec(DialogueOpenPayload::write, DialogueOpenPayload::read);

    private void write(RegistryFriendlyByteBuf buffer) {
        NpcDetailPayload.STREAM_CODEC.encode(buffer, this.npc);
        buffer.writeNbt(this.dialogue.write());
    }

    private static DialogueOpenPayload read(RegistryFriendlyByteBuf buffer) {
        return new DialogueOpenPayload(NpcDetailPayload.STREAM_CODEC.decode(buffer), Dialogue.read(buffer.readNbt()));
    }

    @Override
    public Type<DialogueOpenPayload> type() {
        return TYPE;
    }
}
