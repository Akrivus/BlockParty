package block_party.network.payload;

import block_party.BlockParty;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ShrineListRequestPayload() implements CustomPacketPayload {
    public static final ShrineListRequestPayload INSTANCE = new ShrineListRequestPayload();
    public static final Type<ShrineListRequestPayload> TYPE = new Type<>(BlockParty.source("shrine_list_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShrineListRequestPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<ShrineListRequestPayload> type() {
        return TYPE;
    }
}
