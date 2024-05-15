package block_party.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class YearbookPageItem extends Item {
    public YearbookPageItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> list, TooltipFlag flag) {
        if (stack.hasTag()) {
            CompoundTag tag = stack.getTag().getCompound("NPC");
            String name = String.format("%s %s", tag.getString("FamilyName"), tag.getString("GivenName"));
            list.add(Component.literal(name).withStyle(ChatFormatting.GRAY));
        }
    }
}
