package block_party.network.payload;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.world.CellPhone;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record NpcCallRequestPayload(long databaseId) implements CustomPacketPayload {
    public static final Type<NpcCallRequestPayload> TYPE = new Type<>(BlockParty.source("npc_call_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NpcCallRequestPayload> STREAM_CODEC =
            CustomPacketPayload.codec(NpcCallRequestPayload::write, NpcCallRequestPayload::read);

    public static void handle(NpcCallRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                CellPhone.queue(BlockPartyDB.get(player.level()), player, payload.databaseId());
            }
        });
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarLong(this.databaseId);
    }

    private static NpcCallRequestPayload read(RegistryFriendlyByteBuf buffer) {
        return new NpcCallRequestPayload(buffer.readVarLong());
    }

    @Override
    public Type<NpcCallRequestPayload> type() {
        return TYPE;
    }
}
