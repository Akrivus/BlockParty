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

import java.util.Iterator;
import java.util.UUID;

public class YearbookItem extends Item {
    
    public YearbookItem() {
        super(new Properties().group(MoeItems.CreativeTab.INSTANCE));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote()) { return ActionResult.resultPass(stack); }
        DatingSim sim = DatingData.get(world, player.getUniqueID());
        Iterator<UUID> it = sim.characters.keySet().iterator();
        if (it.hasNext()) { MoeMessages.send(player, new SOpenYearbook(hand, sim, it.next())); }
        return ActionResult.resultSuccess(stack);
    }
    
    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (!(entity instanceof AbstractNPCEntity) || entity.world.isRemote()) { return ActionResultType.PASS; }
        AbstractNPCEntity character = (AbstractNPCEntity) entity;
        if (character.getProtagonist().equals(player)) {
            DatingSim sim = DatingData.get(player.world, player.getUniqueID());
            MoeMessages.send(player, new SOpenYearbook(hand, sim, entity.getUniqueID()));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }
}
