package moe.blocks.mod.message;

import moe.blocks.mod.client.screen.YearbookScreen;
import moe.blocks.mod.data.yearbook.Book;
import moe.blocks.mod.init.MoeItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

import java.time.Year;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class SOpenYearbook {
    protected final Book book;
    protected final UUID pageUUID;

    public SOpenYearbook(Book book, UUID pageUUID) {
        this.book = book;
        this.pageUUID = pageUUID;
    }

    public SOpenYearbook(Book book) {
        this(book, book.isEmpty() ? new UUID(0, 0) : book.getPages()[0].getUUID());
    }

    public SOpenYearbook(PacketBuffer buffer) {
        this(new Book(buffer.readCompoundTag()), buffer.readUniqueId());
    }

    public Book getBook() {
        return this.book;
    }

    public UUID getPageUUID() {
        return this.pageUUID;
    }

    public static void encode(SOpenYearbook message, PacketBuffer buffer) {
        buffer.writeCompoundTag(message.getBook().write());
        buffer.writeUniqueId(message.getPageUUID());
    }

    public static void handle(SOpenYearbook message, NetworkEvent.Context context, Minecraft mc) {
        if (mc.player.getHeldItem(Hand.MAIN_HAND).getItem() != MoeItems.YEARBOOK.get()) { return; }
        if (message.getBook().isEmpty()) {
            mc.player.sendStatusMessage(new TranslationTextComponent("command.moeblocks.yearbook.error"), true);
        } else {
            mc.displayGuiScreen(new YearbookScreen(message.getBook(), message.getPageUUID()));
        }
    }

    public static void handleContext(SOpenYearbook message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> handle(message, context.get(), Minecraft.getInstance()));
        context.get().setPacketHandled(true);
    }
}
