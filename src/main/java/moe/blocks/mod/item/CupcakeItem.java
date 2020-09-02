package moe.blocks.mod.item;

import moe.blocks.mod.init.MoeItems;
import net.minecraft.item.Food;
import net.minecraft.item.Item;

public class CupcakeItem extends Item {
    public static final Food FOOD = new Food.Builder().hunger(2).saturation(0.1F).build();

    public CupcakeItem() {
        super(new Properties().group(MoeItems.Group.INSTANCE).food(FOOD));
    }
}
