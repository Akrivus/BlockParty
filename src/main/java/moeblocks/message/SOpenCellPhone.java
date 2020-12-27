package moeblocks.message;

import moeblocks.client.screen.CellPhoneScreen;
import moeblocks.client.screen.ControllerScreen;
import moeblocks.init.MoeItems;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.UUID;

public class SOpenCellPhone extends SOpenController {
    public SOpenCellPhone(List<UUID> npcs, Hand hand) {
        super(npcs, UUID.randomUUID(), hand);
    }
    
    public SOpenCellPhone(PacketBuffer buffer) {
        super(buffer);
    }
    
    @Override
    protected Item getItem() {
        return MoeItems.CELL_PHONE.get();
    }
    
    @Override
    protected ControllerScreen getScreen() {
        return new CellPhoneScreen(this.npcs);
    }
}
