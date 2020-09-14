package moe.blocks.mod.item;

import moe.blocks.mod.data.Yearbooks;
import moe.blocks.mod.init.MoeItems;
import moe.blocks.mod.init.MoeMessages;
import moe.blocks.mod.message.YearbookMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class YearbookItem extends Item {

    public YearbookItem() {
        super(new Properties().group(MoeItems.Group.INSTANCE));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (world.isRemote()) { return super.onItemRightClick(world, player, hand); }
        MoeMessages.send(new YearbookMessage.Open(Yearbooks.get(player), 0));
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }
}
