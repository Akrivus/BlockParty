package block_party.network.payload;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.network.ClientPayloadBridge;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;
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

    public static ControllerOpenPayload cellPhone(BlockPartyDB db, UUID player, InteractionHand hand) {
        List<NpcDetailPayload> details = new ArrayList<>();
        for (long databaseId : db.listPhoneContactNpcIds(player)) {
            NpcDetailPayload detail = NpcDetailPayload.phoneResponse(db, player, databaseId);
            if (detail.found()) {
                details.add(detail);
            }
        }
        return cellPhone(details, hand);
    }

    public static ControllerOpenPayload yearbook(BlockPartyDB db, UUID player, long selectedDatabaseId, InteractionHand hand) {
        return yearbook(controllerDetails(db, player), selectedDatabaseId, hand);
    }

    public static void handle(ControllerOpenPayload payload, IPayloadContext context) {
        ClientPayloadBridge.handle("openController", ControllerOpenPayload.class, payload);
    }

    private static List<NpcDetailPayload> controllerDetails(BlockPartyDB db, UUID player) {
        List<NpcDetailPayload> details = new ArrayList<>();
        for (long databaseId : db.listYearbookNpcIds(player)) {
            NpcDetailPayload detail = NpcDetailPayload.response(db, player, databaseId);
            if (detail.found()) {
                details.add(detail);
            }
        }
        return List.copyOf(details);
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
