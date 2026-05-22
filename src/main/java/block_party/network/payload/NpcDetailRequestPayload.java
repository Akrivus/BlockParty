package block_party.network.payload;

import block_party.BlockParty;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record NpcDetailRequestPayload(long databaseId) implements CustomPacketPayload {
    public static final Type<NpcDetailRequestPayload> TYPE = new Type<>(BlockParty.source("npc_detail_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NpcDetailRequestPayload> STREAM_CODEC =
            CustomPacketPayload.codec(NpcDetailRequestPayload::write, NpcDetailRequestPayload::read);

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarLong(this.databaseId);
    }

    private static NpcDetailRequestPayload read(RegistryFriendlyByteBuf buffer) {
        return new NpcDetailRequestPayload(buffer.readVarLong());
    }

    @Override
    public Type<NpcDetailRequestPayload> type() {
        return TYPE;
    }
}
