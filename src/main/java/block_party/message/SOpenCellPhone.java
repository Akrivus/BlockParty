package block_party.message;

import block_party.client.screen.CellPhoneScreen;
import block_party.client.screen.ControllerScreen;
import block_party.init.BlockPartyItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;

import java.util.List;

public class SOpenCellPhone extends SOpenController {
    public SOpenCellPhone(List<Long> npcs, InteractionHand hand) {
        super(npcs, -1, hand);
    }

    public SOpenCellPhone(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected Item getItem() {
        return BlockPartyItems.CELL_PHONE.get();
    }

    @Override
    protected ControllerScreen getScreen() {
        return new CellPhoneScreen(this.npcs);
    }
}
