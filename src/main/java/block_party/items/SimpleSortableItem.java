package block_party.items;

import net.minecraft.world.item.Item;

public class SimpleSortableItem extends Item implements SortableItem {
    private final int sortOrder;

    public SimpleSortableItem(Properties properties, int sortOrder) {
        super(properties);
        this.sortOrder = sortOrder;
    }

    @Override
    public int getSortOrder() {
        return this.sortOrder;
    }
}
