package block_party.network.payload;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.network.ClientPayloadBridge;
import block_party.scene.Dialogue;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record DialogueOpenPayload(NpcDetailPayload npc, Dialogue dialogue) implements CustomPacketPayload {
    public static final Type<DialogueOpenPayload> TYPE = new Type<>(BlockParty.source("dialogue_open"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueOpenPayload> STREAM_CODEC =
            CustomPacketPayload.codec(DialogueOpenPayload::write, DialogueOpenPayload::read);

    public static DialogueOpenPayload response(BlockPartyDB db, UUID owner, long databaseId, Dialogue dialogue) {
        return new DialogueOpenPayload(NpcDetailPayload.response(db, owner, databaseId), dialogue);
    }

    public static void handle(DialogueOpenPayload payload, IPayloadContext context) {
        ClientPayloadBridge.handle("openDialogue", DialogueOpenPayload.class, payload);
    }

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
