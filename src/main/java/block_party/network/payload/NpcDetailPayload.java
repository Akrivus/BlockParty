package block_party.network.payload;

import block_party.BlockParty;
import block_party.db.records.NPC;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.UUID;

public record NpcDetailPayload(
        long databaseId,
        boolean found,
        UUID ownerUuid,
        String name,
        String gender,
        int blockStateId,
        boolean hiding,
        BlockPos hiddenPos) implements CustomPacketPayload {
    private static final UUID EMPTY_UUID = new UUID(0L, 0L);

    public static final Type<NpcDetailPayload> TYPE = new Type<>(BlockParty.source("npc_detail"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NpcDetailPayload> STREAM_CODEC =
            CustomPacketPayload.codec(NpcDetailPayload::write, NpcDetailPayload::read);

    public static NpcDetailPayload missing(long databaseId) {
        return new NpcDetailPayload(databaseId, false, EMPTY_UUID, "", "", 0, false, BlockPos.ZERO);
    }

    public static NpcDetailPayload from(long requestedId, Optional<NPC> npc) {
        return npc.map(NpcDetailPayload::from).orElseGet(() -> missing(requestedId));
    }

    public static NpcDetailPayload from(NPC npc) {
        return new NpcDetailPayload(
                npc.databaseId(),
                true,
                npc.playerUuid(),
                npc.name(),
                npc.gender(),
                Block.getId(npc.blockState()),
                npc.hiding(),
                npc.hiddenPos());
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarLong(this.databaseId);
        buffer.writeBoolean(this.found);
        buffer.writeUUID(this.ownerUuid);
        buffer.writeUtf(this.name);
        buffer.writeUtf(this.gender);
        buffer.writeVarInt(this.blockStateId);
        buffer.writeBoolean(this.hiding);
        buffer.writeLong(this.hiddenPos.asLong());
    }

    private static NpcDetailPayload read(RegistryFriendlyByteBuf buffer) {
        return new NpcDetailPayload(
                buffer.readVarLong(),
                buffer.readBoolean(),
                buffer.readUUID(),
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readVarInt(),
                buffer.readBoolean(),
                BlockPos.of(buffer.readLong()));
    }

    @Override
    public Type<NpcDetailPayload> type() {
        return TYPE;
    }
}
