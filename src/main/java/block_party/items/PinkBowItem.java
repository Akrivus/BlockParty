package block_party.items;

import block_party.utils.sorters.ISortableItem;
import net.minecraft.world.item.Item;

public class PinkBowItem extends Item implements ISortableItem {
    public PinkBowItem() {
        super(new Properties());
    }

    @Override
    public int getSortOrder() {
        return 10;
    }
}
