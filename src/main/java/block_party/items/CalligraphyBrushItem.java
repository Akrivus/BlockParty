package block_party.items;

import block_party.BlockParty;
import block_party.util.sort.ISortableItem;
import net.minecraft.world.item.Item;

public class CalligraphyBrushItem extends Item implements ISortableItem {
    public CalligraphyBrushItem() {
        super(new Properties().tab(BlockParty.ITEMS).stacksTo(1).durability(64));
    }

    @Override
    public int getSortOrder() {
        return 5;
    }
}
