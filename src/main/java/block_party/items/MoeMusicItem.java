package block_party.items;

import block_party.BlockParty;
import block_party.utils.sorters.ISortableItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;

import java.util.function.Supplier;

public class MoeMusicItem extends RecordItem implements ISortableItem {
    public MoeMusicItem(Supplier<SoundEvent> sound, int duration) {
        super(0, sound, new Item.Properties().stacksTo(1), duration);
    }

    @Override
    public int getSortOrder() {
        return 100;
    }
}
