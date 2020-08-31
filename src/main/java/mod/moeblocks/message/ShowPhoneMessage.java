package mod.moeblocks.message;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ShowPhoneMessage {

    public static ShowPhoneMessage decode(PacketBuffer buffer) {
        return null;
    }

    public static void encode(ShowPhoneMessage message, PacketBuffer buffer) {

    }

    public static void handle(ShowPhoneMessage message, Supplier<NetworkEvent.Context> context) {

    }
}
