package moe.blocks.mod.message;

import moe.blocks.mod.client.screen.YearbookScreen;
import moe.blocks.mod.data.Yearbooks;
import moe.blocks.mod.init.MoeItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SOpenYearbook {
    protected final Hand hand;
    protected final Yearbooks.Book book;
    protected final int pageNumber;

    public SOpenYearbook(Hand hand, Yearbooks.Book book, int pageNumber) {
        this.hand = hand;
        this.book = book;
        this.pageNumber = pageNumber;
    }

    public SOpenYearbook(PacketBuffer buffer) {
        this(buffer.readEnumValue(Hand.class), new Yearbooks.Book(buffer.readCompoundTag()), buffer.readInt());
    }

    public Hand getHand() {
        return this.hand;
    }

    public Yearbooks.Book getBook() {
        return this.book;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public static void encode(SOpenYearbook message, PacketBuffer buffer) {
        buffer.writeEnumValue(message.getHand());
        buffer.writeCompoundTag(message.getBook().write());
        buffer.writeInt(message.getPageNumber());
    }

    public static void handle(SOpenYearbook message, NetworkEvent.Context context, Minecraft mc) {
        if (mc.player.getHeldItem(message.getHand()).getItem() != MoeItems.YEARBOOK.get()) { return; }
        if (mc.currentScreen instanceof YearbookScreen) { return; } // Fixes indexes not persisting
        if (message.getBook().isEmpty()) {
            mc.player.sendStatusMessage(new TranslationTextComponent("command.moeblocks.yearbook.error"), true);
        } else {
            mc.displayGuiScreen(new YearbookScreen(message.getBook(), message.getPageNumber()));
        }
    }

    public static void handleContext(SOpenYearbook message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> handle(message, context.get(), Minecraft.getInstance()));
        context.get().setPacketHandled(true);
    }
}
