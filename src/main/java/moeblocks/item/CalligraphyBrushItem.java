package moeblocks.item;

import moeblocks.MoeMod;
import moeblocks.util.sort.ISortableItem;
import net.minecraft.item.Item;

public class CalligraphyBrushItem extends Item implements ISortableItem {
    public CalligraphyBrushItem() {
        super(new Properties().group(MoeMod.ITEMS).maxStackSize(1).maxDamage(64));
    }

    @Override
    public int getSortOrder() {
        return 5;
    }
}
