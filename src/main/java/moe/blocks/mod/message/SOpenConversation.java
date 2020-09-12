package moe.blocks.mod.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SOpenConversation {

    public static SOpenConversation decode(PacketBuffer buffer) {
        return null;
    }

    public static void encode(SOpenConversation message, PacketBuffer buffer) {

    }

    public static void handle(SOpenConversation message, Supplier<NetworkEvent.Context> context) {

    }
}
