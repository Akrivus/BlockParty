package moe.blocks.mod.message;

import moe.blocks.mod.client.screen.YearBookScreen;
import moe.blocks.mod.init.MoeItems;
import moe.blocks.mod.item.YearBookItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SOpenYearBook {
    public final ItemStack stack;
    public final UUID uuid;
    public final int page;

    public SOpenYearBook(ItemStack stack, PlayerEntity player) {
        this(stack, player.getUniqueID(), 0);
    }

    public SOpenYearBook(ItemStack stack, UUID uuid, int page) {
        this.stack = stack;
        this.uuid = uuid;
        this.page = page;
    }

    public SOpenYearBook(ItemStack stack, PlayerEntity player, int page) {
        this(stack, player.getUniqueID(), page);
    }

    public static SOpenYearBook decode(PacketBuffer buffer) {
        return new SOpenYearBook(buffer.readItemStack(), UUID.fromString(buffer.readString()), buffer.readInt());
    }

    public static void encode(SOpenYearBook message, PacketBuffer buffer) {
        buffer.writeItemStack(message.stack);
        buffer.writeString(message.uuid.toString());
        buffer.writeInt(message.page);
    }

    public static void handle(SOpenYearBook message, Supplier<NetworkEvent.Context> context) {
        context.get().setPacketHandled(true);
        context.get().enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ItemStack stack = message.stack;
            if (stack.getItem() == MoeItems.YEARBOOK.get() && YearBookItem.getPageCount(stack) > 0) {
                client.displayGuiScreen(new YearBookScreen(stack, message.uuid, message.page));
            } else {
                client.player.sendStatusMessage(new TranslationTextComponent("command.moeblocks.yearbook.pass"), true);
            }
        });
    }
}