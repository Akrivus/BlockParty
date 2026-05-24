package block_party.network.payload;

import block_party.BlockParty;
import block_party.scene.Response;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DialogueRespondPayload(long databaseId, Response response) implements CustomPacketPayload {
    public static final Type<DialogueRespondPayload> TYPE = new Type<>(BlockParty.source("dialogue_respond"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueRespondPayload> STREAM_CODEC =
            CustomPacketPayload.codec(DialogueRespondPayload::write, DialogueRespondPayload::read);

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarLong(this.databaseId);
        buffer.writeEnum(this.response);
    }

    private static DialogueRespondPayload read(RegistryFriendlyByteBuf buffer) {
        return new DialogueRespondPayload(buffer.readVarLong(), buffer.readEnum(Response.class));
    }

    @Override
    public Type<DialogueRespondPayload> type() {
        return TYPE;
    }
}
