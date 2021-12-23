package block_party.messages;

import block_party.client.screens.CellPhoneScreen;
import block_party.client.screens.ControllerScreen;
import block_party.custom.CustomItems;
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
        return CustomItems.CELL_PHONE.get();
    }

    @Override
    protected ControllerScreen getScreen() {
        return new CellPhoneScreen(this.npcs);
    }
}
