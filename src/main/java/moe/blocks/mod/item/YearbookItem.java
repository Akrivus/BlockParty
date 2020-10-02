package moe.blocks.mod.item;

import moe.blocks.mod.data.Yearbooks;
import moe.blocks.mod.entity.AbstractNPCEntity;
import moe.blocks.mod.init.MoeItems;
import moe.blocks.mod.init.MoeMessages;
import moe.blocks.mod.message.SOpenYearbook;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class YearbookItem extends Item {

    public YearbookItem() {
        super(new Properties().group(MoeItems.Group.INSTANCE));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!world.isRemote()) { MoeMessages.send(player, new SOpenYearbook(hand, Yearbooks.getBook(player), 0)); }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!(entity instanceof AbstractNPCEntity)) { return ActionResultType.PASS; }
        AbstractNPCEntity character = (AbstractNPCEntity) entity;
        if (character.isLocal()) {
            Yearbooks.Book book = Yearbooks.getBook(player);
            MoeMessages.send(player, new SOpenYearbook(hand, book, book.setPageIgnorantly(character, player.getUniqueID())));
        }
        return ActionResultType.SUCCESS;
    }
}
