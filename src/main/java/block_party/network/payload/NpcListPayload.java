package block_party.network.payload;

import block_party.BlockParty;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public record NpcListPayload(List<Long> databaseIds) implements CustomPacketPayload {
    public static final Type<NpcListPayload> TYPE = new Type<>(BlockParty.source("npc_list"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NpcListPayload> STREAM_CODEC =
            CustomPacketPayload.codec(NpcListPayload::write, NpcListPayload::read);

    public NpcListPayload {
        databaseIds = List.copyOf(databaseIds);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(this.databaseIds.size());
        for (long databaseId : this.databaseIds) {
            buffer.writeVarLong(databaseId);
        }
    }

    private static NpcListPayload read(RegistryFriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        List<Long> databaseIds = new ArrayList<>(size);
        for (int index = 0; index < size; ++index) {
            databaseIds.add(buffer.readVarLong());
        }
        return new NpcListPayload(databaseIds);
    }

    @Override
    public Type<NpcListPayload> type() {
        return TYPE;
    }
}
