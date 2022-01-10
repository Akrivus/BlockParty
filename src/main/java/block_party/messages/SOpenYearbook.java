package block_party.messages;

import block_party.client.screens.ControllerScreen;
import block_party.client.screens.YearbookScreen;
import block_party.registry.CustomItems;
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
