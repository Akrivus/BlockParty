package block_party.network.payload;

import block_party.BlockParty;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record NpcRemoveRequestPayload(long databaseId) implements CustomPacketPayload {
    public static final Type<NpcRemoveRequestPayload> TYPE = new Type<>(BlockParty.source("npc_remove_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NpcRemoveRequestPayload> STREAM_CODEC =
            CustomPacketPayload.codec(NpcRemoveRequestPayload::write, NpcRemoveRequestPayload::read);

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarLong(this.databaseId);
    }

    private static NpcRemoveRequestPayload read(RegistryFriendlyByteBuf buffer) {
        return new NpcRemoveRequestPayload(buffer.readVarLong());
    }

    @Override
    public Type<NpcRemoveRequestPayload> type() {
        return TYPE;
    }
}
