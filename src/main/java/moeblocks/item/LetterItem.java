package moeblocks.item;

import moeblocks.init.MoeItems;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class LetterItem extends Item {
    public static final IItemPropertyGetter CLOSED_PROPERTY_GETTER = (stack, world, entity) -> LetterItem.isClosed(stack);
    public static final ResourceLocation CLOSED_PROPERTY = new ResourceLocation("closed");
    
    public LetterItem() {
        super(new Properties().group(MoeItems.CreativeTab.INSTANCE));
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
