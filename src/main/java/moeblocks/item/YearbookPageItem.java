package moeblocks.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class YearbookPageItem extends Item {
    
    public YearbookPageItem() {
        super(new Properties().maxStackSize(1));
    }
    
    @Override
    public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
        if (stack.hasTag()) {
            CompoundNBT tag = stack.getShareTag().getCompound("NPC");
            String name = String.format("%s %s", tag.getString("FamilyName"), tag.getString("GivenName"));
            list.add(new StringTextComponent(name).mergeStyle(TextFormatting.GRAY));
        }
    }
}
