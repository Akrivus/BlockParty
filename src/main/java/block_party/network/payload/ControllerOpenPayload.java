package block_party.network.payload;

import block_party.BlockParty;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;

import java.util.ArrayList;
import java.util.List;

public record ControllerOpenPayload(ControllerType controller, List<NpcDetailPayload> npcs, long selectedDatabaseId, InteractionHand hand)
        implements CustomPacketPayload {
    public static final Type<ControllerOpenPayload> TYPE = new Type<>(BlockParty.source("controller_open"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ControllerOpenPayload> STREAM_CODEC =
            CustomPacketPayload.codec(ControllerOpenPayload::write, ControllerOpenPayload::read);

    public ControllerOpenPayload {
        npcs = List.copyOf(npcs);
    }

    public static ControllerOpenPayload cellPhone(List<NpcDetailPayload> npcs, InteractionHand hand) {
        return new ControllerOpenPayload(ControllerType.CELL_PHONE, npcs, -1L, hand);
    }

    public static ControllerOpenPayload yearbook(List<NpcDetailPayload> npcs, long selectedDatabaseId, InteractionHand hand) {
        return new ControllerOpenPayload(ControllerType.YEARBOOK, npcs, selectedDatabaseId, hand);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeEnum(this.controller);
        buffer.writeVarInt(this.npcs.size());
        for (NpcDetailPayload npc : this.npcs) {
            NpcDetailPayload.STREAM_CODEC.encode(buffer, npc);
        }
        buffer.writeVarLong(this.selectedDatabaseId);
        buffer.writeEnum(this.hand);
    }

    private static ControllerOpenPayload read(RegistryFriendlyByteBuf buffer) {
        ControllerType controller = buffer.readEnum(ControllerType.class);
        int size = buffer.readVarInt();
        List<NpcDetailPayload> npcs = new ArrayList<>(size);
        for (int index = 0; index < size; ++index) {
            npcs.add(NpcDetailPayload.STREAM_CODEC.decode(buffer));
        }
        return new ControllerOpenPayload(controller, npcs, buffer.readVarLong(), buffer.readEnum(InteractionHand.class));
    }

    @Override
    public Type<ControllerOpenPayload> type() {
        return TYPE;
    }

    public enum ControllerType {
        CELL_PHONE,
        YEARBOOK
    }
}
