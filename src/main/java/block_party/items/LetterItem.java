package block_party.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class LetterItem extends Item implements SortableItem {
    private static final String IS_CLOSED = "IsClosed";

    public LetterItem(Properties properties) {
        super(properties);
    }

    public static float isOpen(ItemStack stack) {
        return isClosed(stack) < 1.0F ? 1.0F : 0.0F;
    }

    public static float isClosed(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return 0.0F;
        }
        return data.contains(IS_CLOSED) && data.getUnsafe().getBoolean(IS_CLOSED) ? 1.0F : 0.0F;
    }

    @Override
    public int getSortOrder() {
        return 3;
    }
}
