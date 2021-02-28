package moeblocks.item;

import moeblocks.util.sort.ISortableItem;
import net.minecraft.item.Item;

public class PinkBowItem extends Item implements ISortableItem {
    
    public PinkBowItem() {
        super(new Properties());
    }

    @Override
    public int getSortOrder() {
        return 10;
    }
}
