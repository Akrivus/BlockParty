package block_party.init;

import block_party.BlockParty;
import block_party.message.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

import java.util.function.Function;
import java.util.function.Predicate;

public class BlockPartyMessages {
    private static final Predicate<String> version = (version) -> version.equals(BlockParty.getVersion());
    private static int messageID = 0;

    public static SimpleChannel init(ResourceLocation name) {
        return NetworkRegistry.ChannelBuilder.named(name).clientAcceptedVersions(version).serverAcceptedVersions(version).networkProtocolVersion(BlockParty::getVersion).simpleChannel();
    }

    public static void registerServer() {
        register(CDialogueClose.class, CDialogueClose::new);
        register(CDialogueRespond.class, CDialogueRespond::new);
        register(CNPCRemove.class, CNPCRemove::new);
        register(CNPCRequest.class, CNPCRequest::new);
        register(CNPCTeleport.class, CNPCTeleport::new);
        register(CRemovePage.class, CRemovePage::new);
    }

    public static void registerClient() {
        register(SCloseDialogue.class, SCloseDialogue::new);
        register(SNPCList.class, SNPCList::new);
        register(SNPCResponse.class, SNPCResponse::new);
        register(SOpenCellPhone.class, SOpenCellPhone::new);
        register(SOpenDialogue.class, SOpenDialogue::new);
        register(SOpenYearbook.class, SOpenYearbook::new);
        register(SShrineList.class, SShrineList::new);
    }

    public static <T extends AbstractMessage> void register(Class<T> packet, Function<FriendlyByteBuf, T> con) {
        BlockParty.CHANNEL.messageBuilder(packet, ++BlockPartyMessages.messageID).decoder(con).encoder(AbstractMessage::prepare).consumer(AbstractMessage::consume).add();
    }

    public static void send(Player player, AbstractMessage message) {
        if (player instanceof AbstractClientPlayer) { return; }
        Connection network = ((ServerPlayer) player).connection.getConnection();
        BlockParty.CHANNEL.sendTo(message, network, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void send(AbstractMessage message) {
        BlockParty.CHANNEL.sendToServer(message);
    }
}
