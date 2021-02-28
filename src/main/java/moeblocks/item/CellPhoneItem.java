package moeblocks.item;

import moeblocks.MoeMod;
import moeblocks.datingsim.DatingData;
import moeblocks.init.MoeMessages;
import moeblocks.message.SOpenCellPhone;
import moeblocks.util.sort.ISortableItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CellPhoneItem extends Item implements ISortableItem {
    public CellPhoneItem() {
        super(new Properties().group(MoeMod.ITEMS));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (world.isRemote()) { return ActionResult.resultPass(player.getHeldItem(hand)); }
        MoeMessages.send(player, new SOpenCellPhone(DatingData.get(player.world, player.getUniqueID()).getNPCs(), hand));
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    @Override
    public int getSortOrder() {
        return 10;
    }
}
