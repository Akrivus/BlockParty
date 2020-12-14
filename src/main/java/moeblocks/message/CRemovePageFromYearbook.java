package moeblocks.message;

import moeblocks.datingsim.DatingData;
import moeblocks.init.MoeItems;
import moeblocks.item.YearbookPageItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CRemovePageFromYearbook {
    protected final UUID uuid;

    public CRemovePageFromYearbook(PacketBuffer buffer) {
        this(buffer.readUniqueId());
    }

    public CRemovePageFromYearbook(UUID uuid) {
        this.uuid = uuid;
    }

    public static void encode(CRemovePageFromYearbook message, PacketBuffer buffer) {
        buffer.writeUniqueId(message.getUUID());
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public static void handleContext(CRemovePageFromYearbook message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> handle(message, context.get(), context.get().getSender()));
        context.get().setPacketHandled(true);
    }

    public static void handle(CRemovePageFromYearbook message, NetworkEvent.Context context, ServerPlayerEntity player) {
        ItemStack stack = new ItemStack(MoeItems.YEARBOOK_PAGE.get());
        stack.setTag(DatingData.get(player.world, player.getUniqueID()).write(new CompoundNBT()));
        if (!player.addItemStackToInventory(stack)) { player.entityDropItem(stack); }
    }
}
