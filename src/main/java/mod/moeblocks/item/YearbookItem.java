package mod.moeblocks.item;

import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.register.ItemsMoe;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class YearbookItem extends Item {

    public YearbookItem() {
        super(new Properties().group(ItemsMoe.Group.INSTANCE));
    }
}
