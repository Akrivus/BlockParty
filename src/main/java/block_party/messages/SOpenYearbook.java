package block_party.messages;

import block_party.client.screen.ControllerScreen;
import block_party.client.screen.YearbookScreen;
import block_party.custom.CustomItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;

import java.util.List;

public class SOpenYearbook extends SOpenController {
    public SOpenYearbook(List<Long> npcs, long id, InteractionHand hand) {
        super(npcs, id, hand);
    }

    public SOpenYearbook(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected Item getItem() {
        return CustomItems.YEARBOOK.get();
    }

    @Override
    protected ControllerScreen getScreen() {
        return new YearbookScreen(this.npcs, this.id);
    }
}
