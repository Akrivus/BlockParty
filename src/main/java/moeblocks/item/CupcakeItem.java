package moeblocks.item;

import moeblocks.MoeMod;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class CupcakeItem extends Item {
    private static final Food FOOD = new Food.Builder().hunger(2).saturation(0.1F).build();
    
    public CupcakeItem() {
        super(new Properties().group(MoeMod.ITEMS).food(FOOD));
    }
}
