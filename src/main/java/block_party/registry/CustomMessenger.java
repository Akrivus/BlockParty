package block_party.registry;

import block_party.BlockParty;
import block_party.messages.*;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.Function;
import java.util.function.Predicate;

public class CustomMessenger {
    private static final Predicate<String> version = (version) -> version.equals(BlockParty.getVersion());
    private static int messageID = 0;

    public static SimpleChannel create() {
        return NetworkRegistry.ChannelBuilder.named(BlockParty.source(BlockParty.ID)).clientAcceptedVersions(version).serverAcceptedVersions(version).networkProtocolVersion(BlockParty::getVersion).simpleChannel();
    }

    public static void register(IEventBus bus) {
        bus.addListener(CustomMessenger::registerClient);
        bus.addListener(CustomMessenger::registerServer);
    }

    public static void registerServer(FMLCommonSetupEvent e) {
        register(CDialogueClose.class, CDialogueClose::new);
        register(CDialogueRespond.class, CDialogueRespond::new);
        register(CNPCRemove.class, CNPCRemove::new);
        register(CNPCRequest.class, CNPCRequest::new);
        register(CNPCTeleport.class, CNPCTeleport::new);
        register(CRemovePage.class, CRemovePage::new);
    }

    public static void registerClient(FMLClientSetupEvent e) {
        register(SCloseDialogue.class, SCloseDialogue::new);
        register(SNPCList.class, SNPCList::new);
        register(SNPCResponse.class, SNPCResponse::new);
        register(SOpenCellPhone.class, SOpenCellPhone::new);
        register(SOpenDialogue.class, SOpenDialogue::new);
        register(SOpenYearbook.class, SOpenYearbook::new);
        register(SShrineList.class, SShrineList::new);
    }

    public static <T extends AbstractMessage> void register(Class<T> packet, Function<FriendlyByteBuf, T> con) {
        BlockParty.MESSENGER.messageBuilder(packet, ++CustomMessenger.messageID).decoder(con).encoder(AbstractMessage::prepare).consumer(AbstractMessage::consume).add();
    }

    public static void send(Player player, AbstractMessage message) {
        if (player instanceof ServerPlayer server) {
            Connection network = server.connection.connection;
            BlockParty.MESSENGER.sendTo(message, network, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void send(AbstractMessage message) {
        BlockParty.MESSENGER.sendToServer(message);
    }
}
