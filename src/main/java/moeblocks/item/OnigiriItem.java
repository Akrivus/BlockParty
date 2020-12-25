package moeblocks.item;

import moeblocks.init.MoeItems;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class OnigiriItem extends Item {
    private static final Food FOOD = new Food.Builder().hunger(2).saturation(0.5F).build();

    public OnigiriItem() {
        super(new Properties().group(MoeItems.CreativeTab.INSTANCE).food(FOOD));
    }
}
