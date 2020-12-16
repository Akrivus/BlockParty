package moeblocks.item;

import moeblocks.datingsim.DatingData;
import moeblocks.datingsim.DatingSim;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeItems;
import moeblocks.init.MoeMessages;
import moeblocks.message.SOpenYearbook;
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
        super(new Properties().group(MoeItems.CreativeTab.INSTANCE));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (!world.isRemote()) { MoeMessages.send(player, new SOpenYearbook(hand, DatingData.get(world, player.getUniqueID()), 0)); }
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!(entity instanceof AbstractNPCEntity)) { return ActionResultType.PASS; }
        AbstractNPCEntity character = (AbstractNPCEntity) entity;
        if (character.isLocal() && character.getProtagonist().equals(player)) {
            DatingSim sim = DatingData.get(player.world, player.getUniqueID());
            int index = sim.getI(entity.getUniqueID());
            MoeMessages.send(player, new SOpenYearbook(hand, sim, index));
        }
        return ActionResultType.SUCCESS;
    }
}
