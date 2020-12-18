package moeblocks.message;

import moeblocks.client.screen.YearbookScreen;
import moeblocks.datingsim.DatingSim;
import moeblocks.init.MoeItems;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SOpenYearbook {
    protected final Hand hand;
    protected final DatingSim sim;
    protected final UUID uuid;

    public SOpenYearbook(PacketBuffer buffer) {
        this(buffer.readEnumValue(Hand.class), new DatingSim(buffer.readCompoundTag()), buffer.readUniqueId());
    }

    public SOpenYearbook(Hand hand, DatingSim sim, UUID uuid) {
        this.hand = hand;
        this.sim = sim;
        this.uuid = uuid;
    }

    public static void encode(SOpenYearbook message, PacketBuffer buffer) {
        buffer.writeEnumValue(message.getHand());
        buffer.writeCompoundTag(message.getSim().write(new CompoundNBT()));
        buffer.writeUniqueId(message.getUUID());
    }

    public Hand getHand() {
        return this.hand;
    }

    public DatingSim getSim() {
        return this.sim;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public static void handleContext(SOpenYearbook message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> handle(message, context.get(), Minecraft.getInstance()));
        context.get().setPacketHandled(true);
    }

    public static void handle(SOpenYearbook message, NetworkEvent.Context context, Minecraft mc) {
        if (mc.player.getHeldItem(message.getHand()).getItem() != MoeItems.YEARBOOK.get()) { return; }
        if (mc.currentScreen instanceof YearbookScreen) { return; } // Fixes indexes not persisting
        mc.displayGuiScreen(new YearbookScreen(message.getSim(), message.getUUID()));
    }
}
