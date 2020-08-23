package mod.moeblocks.init;

import mod.moeblocks.MoeMod;
import mod.moeblocks.message.SUseYBMessage;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MoeMessages<T> {
    private static final ArrayList<MoeMessages<?>> MESSAGES = new ArrayList<>();
    private static int currentMessageID = 1;

    public static final MoeMessages<SUseYBMessage> USE_YEARBOOK = new MoeMessages<>(SUseYBMessage.class, SUseYBMessage::decode, SUseYBMessage::encode, SUseYBMessage::handle);

    public static SimpleChannel register() {
        SimpleChannel channel = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MoeMod.ID, "gui")).clientAcceptedVersions(version -> true).serverAcceptedVersions(version -> true).networkProtocolVersion(() -> MoeMod.VERSION).simpleChannel();
        MESSAGES.forEach(message -> {
            channel.messageBuilder(message.getPacket(), message.getID()).decoder(message.getDecoder()).encoder(message.getEncoder()).consumer(message.getHandler()).add();
        });
        return channel;
    }

    private final Function decoder;
    private final BiConsumer encoder;
    private final BiConsumer handler;
    private final Class packet;
    private final int messageID;

    public <T> MoeMessages(Class packet, Function<PacketBuffer, T> decoder, BiConsumer<T, PacketBuffer> encoder, BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        this.messageID = ++MoeMessages.currentMessageID;
        this.decoder = decoder;
        this.encoder = encoder;
        this.handler = handler;
        this.packet = packet;
        MESSAGES.add(this);
    }

    public Function getDecoder() {
        return this.decoder;
    }

    public BiConsumer getEncoder() {
        return this.encoder;
    }

    public BiConsumer getHandler() {
        return this.handler;
    }

    public Class getPacket() {
        return this.packet;
    }

    public int getID() {
        return this.messageID;
    }
}
