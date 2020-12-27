package moeblocks.item;

import moeblocks.datingsim.DatingData;
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

import java.util.List;
import java.util.UUID;

public class YearbookItem extends Item {
    public YearbookItem() {
        super(new Properties().group(MoeItems.CreativeTab.INSTANCE));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (world.isRemote()) { return ActionResult.resultPass(player.getHeldItem(hand)); }
        return new ActionResult(this.openGui(player, hand, null), player.getHeldItem(hand));
    }
    
    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (player.world.isRemote()) { return ActionResultType.PASS; }
        if (entity instanceof AbstractNPCEntity) {
            AbstractNPCEntity npc = (AbstractNPCEntity) entity;
            if (npc.getProtagonist().equals(player)) { return this.openGui(player, hand, npc.getUUID()); }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }
    
    private ActionResultType openGui(PlayerEntity player, Hand hand, UUID uuid) {
        List<UUID> npcs = DatingData.get(player.world, player.getUniqueID()).getNPCs();
        MoeMessages.send(player, new SOpenYearbook(npcs, uuid == null ? npcs.get(0) : uuid, hand));
        return ActionResultType.SUCCESS;
    }
}
