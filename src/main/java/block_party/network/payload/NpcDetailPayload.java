package block_party.network.payload;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.NPC;
import block_party.entities.Moe;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.UUID;

public record NpcDetailPayload(
        long databaseId,
        boolean found,
        UUID ownerUuid,
        String name,
        String gender,
        boolean dead,
        String bloodType,
        String dere,
        String zodiac,
        float health,
        float foodLevel,
        float loyalty,
        float stress,
        int blockStateId,
        boolean hiding,
        BlockPos hiddenPos) implements CustomPacketPayload {
    private static final UUID EMPTY_UUID = new UUID(0L, 0L);

    public static final Type<NpcDetailPayload> TYPE = new Type<>(BlockParty.source("npc_detail"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NpcDetailPayload> STREAM_CODEC =
            CustomPacketPayload.codec(NpcDetailPayload::write, NpcDetailPayload::read);

    public UUID playerUuid() {
        return this.ownerUuid;
    }

    public static NpcDetailPayload missing(long databaseId) {
        return new NpcDetailPayload(databaseId, false, EMPTY_UUID, "", "", false, "", "", "", 0.0F, 0.0F, 0.0F, 0.0F, 0, false, BlockPos.ZERO);
    }

    public static NpcDetailPayload response(BlockPartyDB db, UUID player, long databaseId) {
        return from(db, player, databaseId, db.loadYearbookNpc(player, databaseId));
    }

    public static NpcDetailPayload relationshipResponse(BlockPartyDB db, UUID player, long databaseId) {
        return from(db, player, databaseId, db.loadRelatedNpc(player, databaseId));
    }

    public static NpcDetailPayload phoneResponse(BlockPartyDB db, UUID player, long databaseId) {
        return from(db, player, databaseId, db.loadPhoneContactNpc(player, databaseId));
    }

    public static NpcDetailPayload response(ServerLevel level, BlockPartyDB db, UUID player, long databaseId) {
        Optional<NPC> row = db.loadYearbookNpc(player, databaseId);
        return from(db, player, databaseId, row, db.findRelatedLoadedMoe(level, player, databaseId));
    }

    public static NpcDetailPayload from(long requestedId, Optional<NPC> npc) {
        return npc.map(NpcDetailPayload::from).orElseGet(() -> missing(requestedId));
    }

    public static NpcDetailPayload from(long requestedId, Optional<NPC> npc, Optional<Moe> live) {
        return npc.map(value -> from(value, live.map(Moe::getHealth).orElse(value.health()))).orElseGet(() -> missing(requestedId));
    }

    public static NpcDetailPayload from(BlockPartyDB db, UUID player, long requestedId, Optional<NPC> npc) {
        return npc.map(value -> from(value, feelings(db, player, value), value.health())).orElseGet(() -> missing(requestedId));
    }

    public static NpcDetailPayload from(BlockPartyDB db, UUID player, long requestedId, Optional<NPC> npc, Optional<Moe> live) {
        return npc.map(value -> from(value, feelings(db, player, value), live.map(Moe::getHealth).orElse(value.health())))
                .orElseGet(() -> missing(requestedId));
    }

    public static NpcDetailPayload from(NPC npc) {
        return from(npc, npc.health());
    }

    public static NpcDetailPayload from(NPC npc, float health) {
        return from(npc, new Feelings(npc.affection(), npc.loyalty()), health);
    }

    public static NpcDetailPayload from(NPC npc, Feelings feelings, float health) {
        return new NpcDetailPayload(
                npc.databaseId(),
                true,
                npc.playerUuid(),
                npc.name(),
                npc.gender(),
                npc.dead(),
                npc.bloodType(),
                npc.dere(),
                npc.zodiac(),
                health,
                npc.foodLevel(),
                feelings.loyalty(),
                npc.stress(),
                Block.getId(npc.blockState()),
                npc.hiding(),
                npc.hiddenPos());
    }

    private static Feelings feelings(BlockPartyDB db, UUID player, NPC npc) {
        return db.findPlayerRelationshipSafe(npc.databaseId(), player)
                .map(relationship -> new Feelings(relationship.affection(), relationship.loyalty()))
                .orElseGet(() -> new Feelings(npc.affection(), npc.loyalty()));
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarLong(this.databaseId);
        buffer.writeBoolean(this.found);
        buffer.writeUUID(this.ownerUuid);
        buffer.writeUtf(this.name);
        buffer.writeUtf(this.gender);
        buffer.writeBoolean(this.dead);
        buffer.writeUtf(this.bloodType);
        buffer.writeUtf(this.dere);
        buffer.writeUtf(this.zodiac);
        buffer.writeFloat(this.health);
        buffer.writeFloat(this.foodLevel);
        buffer.writeFloat(this.loyalty);
        buffer.writeFloat(this.stress);
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
                buffer.readBoolean(),
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readUtf(),
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readVarInt(),
                buffer.readBoolean(),
                BlockPos.of(buffer.readLong()));
    }

    @Override
    public Type<NpcDetailPayload> type() {
        return TYPE;
    }

    public record Feelings(float affection, float loyalty) {
    }
}
