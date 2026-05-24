package block_party.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class MoeBlockItem extends BlockItem implements SortableItem {
    private final int sortOrder;

    public MoeBlockItem(Block block, Item.Properties properties) {
        this(block, properties, 100);
    }

    public MoeBlockItem(Block block, Item.Properties properties, int sortOrder) {
        super(block, properties);
        this.sortOrder = sortOrder;
    }

    @Override
    public int getSortOrder() {
        return this.sortOrder;
    }
}
