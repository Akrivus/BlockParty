package moeblocks.item;

import moeblocks.MoeMod;
import moeblocks.automata.trait.Dere;
import moeblocks.entity.MoeEntity;
import moeblocks.util.sort.ISortableItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class SpawnMoeItem extends Item implements ISortableItem {

    public SpawnMoeItem() {
        super(new Properties().group(MoeMod.ITEMS));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (world.isRemote()) { return ActionResultType.PASS; }
        PlayerEntity player = context.getPlayer();
        BlockPos block = context.getPos();
        BlockPos spawn = block.offset(context.getFace());
        if (MoeEntity.spawn(world, block, spawn, player.rotationYaw, player.rotationPitch, Dere.random(), player)) {
            context.getItem().shrink(1);
            return ActionResultType.CONSUME;
        } else {
            String name = world.getBlockState(block).getBlock().getTranslatedName().getString();
            player.sendStatusMessage(new TranslationTextComponent("item.moeblocks.moe_spawn_egg.error", name), true);
            return ActionResultType.FAIL;
        }
    }

    @Override
    public int getSortOrder() {
        return 1;
    }
}