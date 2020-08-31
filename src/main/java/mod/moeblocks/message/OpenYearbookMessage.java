package mod.moeblocks.message;

import mod.moeblocks.client.screen.YearbookScreen;
import mod.moeblocks.init.MoeItems;
import mod.moeblocks.item.YearbookItem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class OpenYearbookMessage {
    public final ItemStack stack;
    public final UUID uuid;
    public final int page;

    public OpenYearbookMessage(ItemStack stack, PlayerEntity player) {
        this(stack, player.getUniqueID(), 0);
    }

    public OpenYearbookMessage(ItemStack stack, PlayerEntity player, int page) {
        this(stack, player.getUniqueID(), page);
    }

    public OpenYearbookMessage(ItemStack stack, UUID uuid, int page) {
        this.stack = stack;
        this.uuid = uuid;
        this.page = page;
    }

    public static OpenYearbookMessage decode(PacketBuffer buffer) {
        return new OpenYearbookMessage(buffer.readItemStack(), UUID.fromString(buffer.readString()), buffer.readInt());
    }

    public static void encode(OpenYearbookMessage message, PacketBuffer buffer) {
        buffer.writeItemStack(message.stack);
        buffer.writeString(message.uuid.toString());
        buffer.writeInt(message.page);
    }

    public static void handle(OpenYearbookMessage message, Supplier<NetworkEvent.Context> context) {
        context.get().setPacketHandled(true);
        context.get().enqueueWork(() -> {
            Minecraft client = Minecraft.getInstance();
            ItemStack stack = message.stack;
            if (stack.getItem() == MoeItems.YEARBOOK.get() && YearbookItem.getPageCount(stack) > 0) {
                client.displayGuiScreen(new YearbookScreen(stack, message.uuid, message.page));
            } else {
                client.player.sendStatusMessage(new TranslationTextComponent("command.moeblocks.yearbook.pass"), true);
            }
        });
    }
}
