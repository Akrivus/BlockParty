package block_party.network.payload;

import block_party.BlockParty;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record NpcListRequestPayload() implements CustomPacketPayload {
    public static final NpcListRequestPayload INSTANCE = new NpcListRequestPayload();
    public static final Type<NpcListRequestPayload> TYPE = new Type<>(BlockParty.source("npc_list_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NpcListRequestPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<NpcListRequestPayload> type() {
        return TYPE;
    }
}
