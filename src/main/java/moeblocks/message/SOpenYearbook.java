package moeblocks.message;

import moeblocks.client.screen.ControllerScreen;
import moeblocks.client.screen.YearbookScreen;
import moeblocks.init.MoeItems;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.UUID;

public class SOpenYearbook extends SOpenController {
    public SOpenYearbook(List<UUID> npcs, UUID id, Hand hand) {
        super(npcs, id, hand);
    }

    public SOpenYearbook(PacketBuffer buffer) {
        super(buffer);
    }

    @Override
    protected Item getItem() {
        return MoeItems.YEARBOOK.get();
    }

    @Override
    protected ControllerScreen getScreen() {
        return new YearbookScreen(this.npcs, this.id);
    }
}
