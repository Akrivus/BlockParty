package block_party.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class AbstractMessage {
    protected AbstractMessage(FriendlyByteBuf buffer) { }

    protected AbstractMessage() { }

    public static void prepare(AbstractMessage message, FriendlyByteBuf buffer) {
        message.encode(buffer);
    }

    public abstract void encode(FriendlyByteBuf buffer);

    public static void consume(AbstractMessage message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        NetworkDirection direction = context.getDirection();
        if (direction == NetworkDirection.PLAY_TO_CLIENT) {
            context.enqueueWork(() -> message.handle(context, Minecraft.getInstance()));
        }
        if (direction == NetworkDirection.PLAY_TO_SERVER) {
            context.enqueueWork(() -> message.handle(context, context.getSender()));
        }
        context.setPacketHandled(true);
    }

    public abstract void handle(NetworkEvent.Context context, ServerPlayer player);

    public abstract void handle(NetworkEvent.Context context, Minecraft minecraft);

    public abstract static class Client extends AbstractMessage {
        protected Client(FriendlyByteBuf buffer) { }

        protected Client() { }

        public void handle(NetworkEvent.Context context, Minecraft minecraft) { }
    }

    public abstract static class Server extends AbstractMessage {
        protected Server(FriendlyByteBuf buffer) { }

        protected Server() { }

        public void handle(NetworkEvent.Context context, ServerPlayer player) { }
    }
}
