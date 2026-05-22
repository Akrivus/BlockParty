package block_party.network;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.network.payload.NpcDetailPayload;
import block_party.network.payload.NpcDetailRequestPayload;
import block_party.network.payload.NpcListPayload;
import block_party.network.payload.NpcListRequestPayload;
import block_party.network.payload.NpcRemoveRequestPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.UUID;

public final class CustomMessenger {
    private static final String NETWORK_VERSION = "1";

    private CustomMessenger() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(BlockParty.ID).versioned(NETWORK_VERSION).optional();
        registrar.playToServer(NpcListRequestPayload.TYPE, NpcListRequestPayload.STREAM_CODEC, CustomMessenger::handleListRequest);
        registrar.playToServer(NpcDetailRequestPayload.TYPE, NpcDetailRequestPayload.STREAM_CODEC, CustomMessenger::handleDetailRequest);
        registrar.playToServer(NpcRemoveRequestPayload.TYPE, NpcRemoveRequestPayload.STREAM_CODEC, CustomMessenger::handleRemoveRequest);
        registrar.playToClient(NpcListPayload.TYPE, NpcListPayload.STREAM_CODEC, CustomMessenger::handleClientList);
        registrar.playToClient(NpcDetailPayload.TYPE, NpcDetailPayload.STREAM_CODEC, CustomMessenger::handleClientDetail);
    }

    public static NpcListPayload listResponse(BlockPartyDB db, UUID owner) {
        return new NpcListPayload(db.listNpcIds(owner));
    }

    public static NpcDetailPayload detailResponse(BlockPartyDB db, UUID owner, long databaseId) {
        return NpcDetailPayload.from(databaseId, db.loadOwnedNpc(owner, databaseId));
    }

    public static NpcListPayload removeResponse(BlockPartyDB db, UUID owner, long databaseId) {
        db.removeOwnedNpc(owner, databaseId);
        return listResponse(db, owner);
    }

    private static void handleListRequest(NpcListRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> context.reply(listResponse(context.player())));
    }

    private static void handleDetailRequest(NpcDetailRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> context.reply(detailResponse(context.player(), payload.databaseId())));
    }

    private static void handleRemoveRequest(NpcRemoveRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> context.reply(removeResponse(context.player(), payload.databaseId())));
    }

    private static void handleClientList(NpcListPayload payload, IPayloadContext context) {
        // Client UI is intentionally out of scope for the spike.
    }

    private static void handleClientDetail(NpcDetailPayload payload, IPayloadContext context) {
        // Client UI is intentionally out of scope for the spike.
    }

    private static NpcListPayload listResponse(Player player) {
        return listResponse(BlockPartyDB.get(player.level()), player.getUUID());
    }

    private static NpcDetailPayload detailResponse(Player player, long databaseId) {
        return detailResponse(BlockPartyDB.get(player.level()), player.getUUID(), databaseId);
    }

    private static NpcListPayload removeResponse(Player player, long databaseId) {
        return removeResponse(BlockPartyDB.get(player.level()), player.getUUID(), databaseId);
    }
}
