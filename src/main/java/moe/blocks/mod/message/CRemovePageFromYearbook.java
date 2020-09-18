package moe.blocks.mod.message;

import moe.blocks.mod.data.Yearbooks;
import moe.blocks.mod.init.MoeItems;
import moe.blocks.mod.item.YearbookPageItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class CRemovePageFromYearbook {
    protected final UUID pageUUID;

    public CRemovePageFromYearbook(UUID pageUUID) {
        this.pageUUID = pageUUID;
    }

    public CRemovePageFromYearbook(PacketBuffer buffer) {
        this(buffer.readUniqueId());
    }

    public UUID getPageUUID() {
        return this.pageUUID;
    }

    public static void encode(CRemovePageFromYearbook message, PacketBuffer buffer) {
        buffer.writeUniqueId(message.getPageUUID());
    }

    public static void handle(CRemovePageFromYearbook message, NetworkEvent.Context context, ServerPlayerEntity player) {
        ItemStack stack = new ItemStack(MoeItems.YEARBOOK_PAGE.get());
        YearbookPageItem.setPage(stack, Yearbooks.getBook(player).removePage(message.getPageUUID(), player));
        if (!player.addItemStackToInventory(stack)) { player.entityDropItem(stack); }
    }

    public static void handleContext(CRemovePageFromYearbook message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> handle(message, context.get(), context.get().getSender()));
        context.get().setPacketHandled(true);
    }
}
