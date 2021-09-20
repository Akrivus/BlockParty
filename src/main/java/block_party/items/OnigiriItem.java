package block_party.items;

import block_party.BlockParty;
import block_party.utils.sorters.ISortableItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class OnigiriItem extends Item implements ISortableItem {
    private static final FoodProperties FOOD = new FoodProperties.Builder().nutrition(2).saturationMod(0.5F).build();

    public OnigiriItem() {
        super(new Properties().tab(BlockParty.CreativeModeTab).food(FOOD));
    }

    @Override
    public int getSortOrder() {
        return 10;
    }
}
