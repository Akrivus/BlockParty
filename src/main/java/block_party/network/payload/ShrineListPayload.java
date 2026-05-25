package block_party.network.payload;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.network.ClientPayloadBridge;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record ShrineListPayload(List<BlockPos> positions) implements CustomPacketPayload {
    public static final Type<ShrineListPayload> TYPE = new Type<>(BlockParty.source("shrine_list"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShrineListPayload> STREAM_CODEC =
            CustomPacketPayload.codec(ShrineListPayload::write, ShrineListPayload::read);

    public ShrineListPayload {
        positions = List.copyOf(positions);
    }

    public static ShrineListPayload from(List<BlockPartyDB.ShrineEntry> shrines) {
        return new ShrineListPayload(shrines.stream().map(BlockPartyDB.ShrineEntry::pos).toList());
    }

    public static ShrineListPayload response(BlockPartyDB db, UUID player, net.minecraft.resources.ResourceKey<Level> dimension) {
        try {
            return from(db.listShrines(player, dimension));
        } catch (java.sql.SQLException exception) {
            return new ShrineListPayload(List.of());
        }
    }

    public static ShrineListPayload response(Player player) {
        return response(BlockPartyDB.get(player.level()), player.getUUID(), player.level().dimension());
    }

    public static void handle(ShrineListPayload payload, IPayloadContext context) {
        ClientPayloadBridge.handle("handleShrineList", ShrineListPayload.class, payload);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeInt(this.positions.size());
        for (BlockPos pos : this.positions) {
            buffer.writeInt(pos.getX());
            buffer.writeInt(pos.getY());
            buffer.writeInt(pos.getZ());
        }
    }

    private static ShrineListPayload read(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readInt();
        List<BlockPos> positions = new ArrayList<>(size);
        for (int index = 0; index < size; ++index) {
            positions.add(new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt()));
        }
        return new ShrineListPayload(positions);
    }

    @Override
    public Type<ShrineListPayload> type() {
        return TYPE;
    }
}
