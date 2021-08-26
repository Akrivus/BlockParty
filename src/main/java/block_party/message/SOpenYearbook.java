package block_party.message;

import block_party.client.screen.ControllerScreen;
import block_party.client.screen.YearbookScreen;
import block_party.init.BlockPartyItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.UUID;

public class SOpenYearbook extends SOpenController {
    public SOpenYearbook(List<UUID> npcs, UUID id, InteractionHand hand) {
        super(npcs, id, hand);
    }

    public SOpenYearbook(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected Item getItem() {
        return BlockPartyItems.YEARBOOK.get();
    }

    @Override
    protected ControllerScreen getScreen() {
        return new YearbookScreen(this.npcs, this.id);
    }
}
