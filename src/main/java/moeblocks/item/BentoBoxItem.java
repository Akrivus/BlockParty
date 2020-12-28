package moeblocks.item;

import moeblocks.MoeMod;
import moeblocks.init.MoeItems;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class BentoBoxItem extends Item {
    private static final Food FOOD = new Food.Builder().hunger(11).saturation(1.9F).build();
    
    public BentoBoxItem() {
        super(new Properties().group(MoeMod.ITEMS).food(FOOD));
    }
}
