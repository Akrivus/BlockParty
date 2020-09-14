package moe.blocks.mod.message;

import moe.blocks.mod.client.screen.YearbookScreen;
import moe.blocks.mod.data.yearbook.Book;
import moe.blocks.mod.init.MoeItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class YearbookMessage {
    public static class Open {
        public final Book book;
        public final int pageNumber;

        public Open(Book book, int pageNumber) {
            this.book = book;
            this.pageNumber = pageNumber;
        }

        public static Open decode(PacketBuffer buffer) {
            return new Open(new Book(buffer.readCompoundTag()), buffer.readInt());
        }

        public static void encode(Open message, PacketBuffer buffer) {
            buffer.writeCompoundTag(message.book.write());
            buffer.writeInt(message.pageNumber);
        }

        public static void handle(Open message, Supplier<NetworkEvent.Context> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player.getHeldItem(Hand.MAIN_HAND).getItem() != MoeItems.YEARBOOK.get()) { return; }
                if (message.book.getPageCount() == 0) {
                    minecraft.player.sendStatusMessage(new TranslationTextComponent("command.moeblocks.yearbook.error"), true);
                } else {
                    minecraft.displayGuiScreen(new YearbookScreen(message.book, message.pageNumber));
                }
            });
        }
    }
}
