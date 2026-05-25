package block_party.network;

import block_party.BlockParty;
import block_party.network.payload.ControllerOpenPayload;
import block_party.network.payload.DialogueClosePayload;
import block_party.network.payload.DialogueOpenPayload;
import block_party.network.payload.DialogueRespondPayload;
import block_party.network.payload.NpcCallPayload;
import block_party.network.payload.NpcCallRequestPayload;
import block_party.network.payload.NpcRemoveRequestPayload;
import block_party.network.payload.ShrineListPayload;
import block_party.network.payload.ShrineListRequestPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class CustomMessenger {
    private static final String NETWORK_VERSION = "1";

    private CustomMessenger() {
    }

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(BlockParty.ID).versioned(NETWORK_VERSION).optional();
        registrar.playToServer(NpcRemoveRequestPayload.TYPE, NpcRemoveRequestPayload.STREAM_CODEC, NpcRemoveRequestPayload::handle);
        registrar.playToServer(NpcCallRequestPayload.TYPE, NpcCallRequestPayload.STREAM_CODEC, NpcCallRequestPayload::handle);
        registrar.playToServer(DialogueRespondPayload.TYPE, DialogueRespondPayload.STREAM_CODEC, DialogueRespondPayload::handle);
        registrar.playToServer(DialogueClosePayload.TYPE, DialogueClosePayload.STREAM_CODEC, DialogueClosePayload::handle);
        registrar.playToServer(ShrineListRequestPayload.TYPE, ShrineListRequestPayload.STREAM_CODEC, ShrineListRequestPayload::handle);
        registrar.playToClient(NpcCallPayload.TYPE, NpcCallPayload.STREAM_CODEC, NpcCallPayload::handle);
        registrar.playToClient(ControllerOpenPayload.TYPE, ControllerOpenPayload.STREAM_CODEC, ControllerOpenPayload::handle);
        registrar.playToClient(DialogueOpenPayload.TYPE, DialogueOpenPayload.STREAM_CODEC, DialogueOpenPayload::handle);
        registrar.playToClient(ShrineListPayload.TYPE, ShrineListPayload.STREAM_CODEC, ShrineListPayload::handle);
    }

    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sendShrineList(player);
        }
    }

    public static void sendShrineList(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, ShrineListPayload.response(player));
    }
}
