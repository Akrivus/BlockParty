package block_party.network;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.entities.Moe;
import block_party.network.payload.ControllerOpenPayload;
import block_party.network.payload.DialogueClosePayload;
import block_party.network.payload.DialogueOpenPayload;
import block_party.network.payload.DialogueRespondPayload;
import block_party.network.payload.NpcCallPayload;
import block_party.network.payload.NpcCallRequestPayload;
import block_party.network.payload.NpcDetailPayload;
import block_party.network.payload.NpcRemoveRequestPayload;
import block_party.network.payload.ShrineListPayload;
import block_party.network.payload.ShrineListRequestPayload;
import block_party.world.CellPhone;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import block_party.scene.Dialogue;
import block_party.scene.Response;
import java.util.UUID;

public final class CustomMessenger {
    private static final String NETWORK_VERSION = "1";

    private CustomMessenger() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(BlockParty.ID).versioned(NETWORK_VERSION).optional();
        registrar.playToServer(NpcRemoveRequestPayload.TYPE, NpcRemoveRequestPayload.STREAM_CODEC, CustomMessenger::handleRemoveRequest);
        registrar.playToServer(NpcCallRequestPayload.TYPE, NpcCallRequestPayload.STREAM_CODEC, CustomMessenger::handleCallRequest);
        registrar.playToServer(DialogueRespondPayload.TYPE, DialogueRespondPayload.STREAM_CODEC, CustomMessenger::handleDialogueRespond);
        registrar.playToServer(DialogueClosePayload.TYPE, DialogueClosePayload.STREAM_CODEC, CustomMessenger::handleDialogueClose);
        registrar.playToServer(ShrineListRequestPayload.TYPE, ShrineListRequestPayload.STREAM_CODEC, CustomMessenger::handleShrineListRequest);
        registrar.playToClient(NpcCallPayload.TYPE, NpcCallPayload.STREAM_CODEC, CustomMessenger::handleClientCall);
        registrar.playToClient(ControllerOpenPayload.TYPE, ControllerOpenPayload.STREAM_CODEC, CustomMessenger::handleClientControllerOpen);
        registrar.playToClient(DialogueOpenPayload.TYPE, DialogueOpenPayload.STREAM_CODEC, CustomMessenger::handleClientDialogueOpen);
        registrar.playToClient(ShrineListPayload.TYPE, ShrineListPayload.STREAM_CODEC, CustomMessenger::handleClientShrineList);
    }

    public static NpcDetailPayload detailResponse(BlockPartyDB db, UUID owner, long databaseId) {
        return NpcDetailPayload.from(databaseId, db.loadYearbookNpc(owner, databaseId));
    }

    public static NpcDetailPayload detailResponse(ServerLevel level, BlockPartyDB db, UUID owner, long databaseId) {
        java.util.Optional<block_party.db.records.NPC> row = db.loadYearbookNpc(owner, databaseId);
        return NpcDetailPayload.from(databaseId, row, db.findOwnedLoadedMoe(level, owner, databaseId));
    }

    public static void removeResponse(BlockPartyDB db, UUID owner, long databaseId) {
        db.removeOwnedNpc(owner, databaseId);
    }

    public static NpcCallPayload callResponse(ServerLevel level, BlockPartyDB db, UUID owner, BlockPos callerPos, long databaseId) {
        return NpcCallPayload.from(databaseId, db.callOwnedNpc(level, owner, callerPos, databaseId));
    }

    public static ControllerOpenPayload cellPhoneOpenPayload(BlockPartyDB db, UUID owner, InteractionHand hand) {
        return ControllerOpenPayload.cellPhone(controllerDetails(db, owner), hand);
    }

    public static ControllerOpenPayload yearbookOpenPayload(BlockPartyDB db, UUID owner, long selectedDatabaseId, InteractionHand hand) {
        return ControllerOpenPayload.yearbook(controllerDetails(db, owner), selectedDatabaseId, hand);
    }

    public static DialogueOpenPayload dialogueOpenPayload(BlockPartyDB db, UUID owner, long databaseId, Dialogue dialogue) {
        return new DialogueOpenPayload(detailResponse(db, owner, databaseId), dialogue);
    }

    private static java.util.List<NpcDetailPayload> controllerDetails(BlockPartyDB db, UUID owner) {
        java.util.List<NpcDetailPayload> details = new java.util.ArrayList<>();
        for (long databaseId : db.listYearbookNpcIds(owner)) {
            NpcDetailPayload detail = detailResponse(db, owner, databaseId);
            if (detail.found()) {
                details.add(detail);
            }
        }
        return java.util.List.copyOf(details);
    }

    public static boolean respondToDialogue(ServerLevel level, BlockPartyDB db, UUID owner, long databaseId, Response response) {
        java.util.Optional<Moe> moe = db.findOwnedLoadedMoe(level, owner, databaseId);
        if (moe.isEmpty()) {
            return false;
        }
        moe.get().setResponse(response);
        return moe.get().getResponse() == response;
    }

    public static boolean closeDialogue(ServerLevel level, BlockPartyDB db, UUID owner, long databaseId) {
        java.util.Optional<Moe> moe = db.findOwnedLoadedMoe(level, owner, databaseId);
        if (moe.isEmpty()) {
            return false;
        }
        moe.get().clearDialogue();
        return true;
    }

    public static ShrineListPayload shrineListResponse(BlockPartyDB db, UUID owner, net.minecraft.resources.ResourceKey<Level> dimension) {
        try {
            return ShrineListPayload.from(db.listShrines(owner, dimension));
        } catch (java.sql.SQLException exception) {
            return new ShrineListPayload(java.util.List.of());
        }
    }

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sendShrineList(player);
        }
    }

    public static void sendShrineList(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, shrineListResponse(player));
    }

    private static void handleRemoveRequest(NpcRemoveRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> removeResponse(context.player(), payload.databaseId()));
    }

    private static void handleCallRequest(NpcCallRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                CellPhone.queue(BlockPartyDB.get(player.level()), player, payload.databaseId());
            }
        });
    }

    private static void handleDialogueRespond(DialogueRespondPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> respondToDialogue(context.player(), payload.databaseId(), payload.response()));
    }

    private static void handleDialogueClose(DialogueClosePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> closeDialogue(context.player(), payload.databaseId()));
    }

    private static void handleShrineListRequest(ShrineListRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> context.reply(shrineListResponse(context.player())));
    }

    private static void handleClientCall(NpcCallPayload payload, IPayloadContext context) {
        block_party.client.ClientPayloadHandler.handleNpcCall(payload);
    }

    private static void handleClientControllerOpen(ControllerOpenPayload payload, IPayloadContext context) {
        block_party.client.ClientPayloadHandler.openController(payload);
    }

    private static void handleClientDialogueOpen(DialogueOpenPayload payload, IPayloadContext context) {
        block_party.client.ClientPayloadHandler.openDialogue(payload);
    }

    private static void handleClientShrineList(ShrineListPayload payload, IPayloadContext context) {
        block_party.client.ClientPayloadHandler.handleShrineList(payload);
    }

    private static void removeResponse(Player player, long databaseId) {
        removeResponse(BlockPartyDB.get(player.level()), player.getUUID(), databaseId);
    }

    private static NpcCallPayload callResponse(Player player, long databaseId) {
        if (!(player.level() instanceof ServerLevel level)) {
            return new NpcCallPayload(databaseId, false, false, BlockPos.ZERO);
        }
        return NpcCallPayload.from(databaseId, BlockPartyDB.get(level).callOwnedNpc(level, player.getUUID(), player.position(), player.getYRot(), databaseId));
    }

    private static boolean respondToDialogue(Player player, long databaseId, Response response) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        return respondToDialogue(level, BlockPartyDB.get(level), player.getUUID(), databaseId, response);
    }

    private static boolean closeDialogue(Player player, long databaseId) {
        if (!(player.level() instanceof ServerLevel level)) {
            return false;
        }
        return closeDialogue(level, BlockPartyDB.get(level), player.getUUID(), databaseId);
    }

    private static ShrineListPayload shrineListResponse(Player player) {
        return shrineListResponse(BlockPartyDB.get(player.level()), player.getUUID(), player.level().dimension());
    }
}
