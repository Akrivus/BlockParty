package moeblocks.message;

import moeblocks.init.MoeItems;
import moeblocks.item.CellPhoneItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CPhoneRemoveMoe {
    protected final UUID moeUUID;

    public CPhoneRemoveMoe(PacketBuffer buffer) {
        this(buffer.readUniqueId());
    }

    public CPhoneRemoveMoe(UUID moeUUID) {
        this.moeUUID = moeUUID;
    }

    public static void encode(CPhoneRemoveMoe message, PacketBuffer buffer) {
        buffer.writeUniqueId(message.getMoeUUID());
    }

    public UUID getMoeUUID() {
        return this.moeUUID;
    }

    public static void handleContext(CPhoneRemoveMoe message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> handle(message, context.get(), context.get().getSender()));
        context.get().setPacketHandled(true);
    }

    public static void handle(CPhoneRemoveMoe message, NetworkEvent.Context context, ServerPlayerEntity player) {
        Hand hand = player.getHeldItem(Hand.MAIN_HAND).getItem() == MoeItems.CELL_PHONE.get() ? Hand.MAIN_HAND : Hand.OFF_HAND;
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == MoeItems.CELL_PHONE.get()) {
            CellPhoneItem.removeContact(message.getMoeUUID(), stack);
        }
    }
}
