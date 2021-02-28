package moeblocks.item;

import moeblocks.MoeMod;
import moeblocks.util.sort.ISortableItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class CupcakeItem extends Item implements ISortableItem {
    private static final Food FOOD = new Food.Builder().hunger(2).saturation(0.1F).build();
    
    public CupcakeItem() {
        super(new Properties().group(MoeMod.ITEMS).food(FOOD));
    }

    @Override
    public int getSortOrder() {
        return 10;
    }
}
