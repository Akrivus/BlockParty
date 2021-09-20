package block_party.items;

import block_party.BlockParty;
import block_party.utils.sorters.ISortableItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LetterItem extends Item implements ISortableItem {
    public LetterItem() {
        super(new Properties().tab(BlockParty.CreativeModeTab));
    }

    @Override
    public int getSortOrder() {
        return 3;
    }

    public static boolean isOpen(ItemStack stack) {
        return LetterItem.isClosed(stack) < 1.0F;
    }

    public static float isClosed(ItemStack stack) {
        CompoundTag tag = stack.getShareTag();
        if (tag != null && tag.getBoolean("IsClosed")) { return 1.0F; }
        return 0.0F;
    }
}
