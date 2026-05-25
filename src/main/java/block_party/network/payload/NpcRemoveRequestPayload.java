package block_party.network.payload;

import java.util.UUID;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record NpcRemoveRequestPayload(long databaseId) implements CustomPacketPayload {
    public static final Type<NpcRemoveRequestPayload> TYPE = new Type<>(BlockParty.source("npc_remove_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NpcRemoveRequestPayload> STREAM_CODEC =
            CustomPacketPayload.codec(NpcRemoveRequestPayload::write, NpcRemoveRequestPayload::read);

    public static void handle(NpcRemoveRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> NpcRemoveRequestPayload.removeResponse(context.player(), payload.databaseId()));
    }

    public static void removeResponse(BlockPartyDB db, UUID player, long databaseId) {
        db.removeYearbookPage(player, databaseId);
    }

    public static void removeResponse(Player player, long databaseId) {
        removeResponse(BlockPartyDB.get(player.level()), player.getUUID(), databaseId);
    }

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
