package block_party.network.payload;

import block_party.BlockParty;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record DialogueClosePayload(long databaseId) implements CustomPacketPayload {
    public static final Type<DialogueClosePayload> TYPE = new Type<>(BlockParty.source("dialogue_close"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DialogueClosePayload> STREAM_CODEC =
            CustomPacketPayload.codec(DialogueClosePayload::write, DialogueClosePayload::read);

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarLong(this.databaseId);
    }

    private static DialogueClosePayload read(RegistryFriendlyByteBuf buffer) {
        return new DialogueClosePayload(buffer.readVarLong());
    }

    @Override
    public Type<DialogueClosePayload> type() {
        return TYPE;
    }
}
