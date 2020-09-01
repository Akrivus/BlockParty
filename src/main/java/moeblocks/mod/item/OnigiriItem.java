package moeblocks.mod.item;

import moeblocks.mod.init.MoeItems;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class OnigiriItem extends Item {
    public static final Food FOOD = new Food.Builder().hunger(2).saturation(0.5F).build();

    public OnigiriItem() {
        super(new Properties().group(MoeItems.Group.INSTANCE).food(FOOD));
    }
}
