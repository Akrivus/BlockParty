package moeblocks.item;

import moeblocks.MoeMod;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeMessages;
import moeblocks.init.MoeWorldData;
import moeblocks.message.SOpenYearbook;
import moeblocks.util.sort.ISortableItem;
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

public class YearbookItem extends Item implements ISortableItem {
    public YearbookItem() {
        super(new Properties().group(MoeMod.ITEMS));
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
            if (npc.getPlayer().equals(player)) { return this.openGui(player, hand, npc.getDatabaseID()); }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.FAIL;
    }

    private ActionResultType openGui(PlayerEntity player, Hand hand, UUID id) {
        List<UUID> npcs = MoeWorldData.get(player.world).getFrom(player);
        if (npcs.size() > 0) { MoeMessages.send(player, new SOpenYearbook(npcs, id == null ? npcs.get(0) : id, hand)); }
        return ActionResultType.SUCCESS;
    }

    @Override
    public int getSortOrder() {
        return 2;
    }
}
