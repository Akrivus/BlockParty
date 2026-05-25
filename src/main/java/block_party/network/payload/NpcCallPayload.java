package block_party.network.payload;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.entities.Moe;
import block_party.network.ClientPayloadBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;
import java.util.UUID;

public record NpcCallPayload(long databaseId, boolean success, boolean following, BlockPos pos) implements CustomPacketPayload {
    public static final Type<NpcCallPayload> TYPE = new Type<>(BlockParty.source("npc_call"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NpcCallPayload> STREAM_CODEC =
            CustomPacketPayload.codec(NpcCallPayload::write, NpcCallPayload::read);

    public static NpcCallPayload from(long databaseId, Optional<Moe> moe) {
        return moe.map(value -> new NpcCallPayload(databaseId, true, value.isFollowing(), value.blockPosition()))
                .orElseGet(() -> new NpcCallPayload(databaseId, false, false, BlockPos.ZERO));
    }

    public static NpcCallPayload response(ServerLevel level, BlockPartyDB db, UUID player, BlockPos callerPos, long databaseId) {
        return from(databaseId, db.callPhoneContactNpc(level, player, callerPos, databaseId));
    }

    public static void handle(NpcCallPayload payload, IPayloadContext context) {
        ClientPayloadBridge.handle("handleNpcCall", NpcCallPayload.class, payload);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarLong(this.databaseId);
        buffer.writeBoolean(this.success);
        buffer.writeBoolean(this.following);
        buffer.writeLong(this.pos.asLong());
    }

    private static NpcCallPayload read(RegistryFriendlyByteBuf buffer) {
        return new NpcCallPayload(buffer.readVarLong(), buffer.readBoolean(), buffer.readBoolean(), BlockPos.of(buffer.readLong()));
    }

    @Override
    public Type<NpcCallPayload> type() {
        return TYPE;
    }
}
