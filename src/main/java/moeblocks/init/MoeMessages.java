package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.message.*;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Function;
import java.util.function.Predicate;

public class MoeMessages {
    private static final Predicate<String> version = (version) -> version.equals(MoeMod.getVersion());
    private static int messageID = 0;

    public static SimpleChannel init(ResourceLocation name) {
        return NetworkRegistry.ChannelBuilder.named(name).clientAcceptedVersions(version).serverAcceptedVersions(version).networkProtocolVersion(MoeMod::getVersion).simpleChannel();
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
        register(SToriiGatesList.class, SToriiGatesList::new);
    }

    public static <T extends AbstractMessage> void register(Class<T> packet, Function<PacketBuffer, T> con) {
        MoeMod.CHANNEL.messageBuilder(packet, ++MoeMessages.messageID).decoder(con).encoder(AbstractMessage::prepare).consumer(AbstractMessage::consume).add();
    }

    public static void send(PlayerEntity player, AbstractMessage message) {
        if (player instanceof AbstractClientPlayerEntity) { return; }
        NetworkManager network = ((ServerPlayerEntity) player).connection.getNetworkManager();
        MoeMod.CHANNEL.sendTo(message, network, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void send(AbstractMessage message) {
        MoeMod.CHANNEL.sendToServer(message);
    }
}
