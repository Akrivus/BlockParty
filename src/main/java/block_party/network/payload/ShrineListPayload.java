package block_party.network.payload;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

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
