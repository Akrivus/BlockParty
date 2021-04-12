package moeblocks.item;

import moeblocks.MoeMod;
import moeblocks.util.sort.ISortableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class LetterItem extends Item implements ISortableItem {
    public LetterItem() {
        super(new Properties().group(MoeMod.ITEMS));
    }

    @Override
    public int getSortOrder() {
        return 3;
    }

    public static boolean isOpen(ItemStack stack) {
        return LetterItem.isClosed(stack) < 1.0F;
    }

    public static float isClosed(ItemStack stack) {
        CompoundNBT tag = stack.getShareTag();
        if (tag != null && tag.getBoolean("IsClosed")) { return 1.0F; }
        return 0.0F;
    }
}
