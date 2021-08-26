package block_party.items;

import block_party.BlockParty;
import block_party.util.sort.ISortableItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;

import java.util.function.Supplier;

public class MoeMusicItem extends RecordItem implements ISortableItem {

    public MoeMusicItem(Supplier<SoundEvent> sound) {
        super(0, sound, new Item.Properties().tab(BlockParty.ITEMS).stacksTo(1));
    }

    @Override
    public int getSortOrder() {
        return 100;
    }
}
