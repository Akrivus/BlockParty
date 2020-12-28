package moeblocks.item;

import moeblocks.MoeMod;
import moeblocks.automata.state.enums.Dere;
import moeblocks.entity.MoeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class MoeSpawnItem extends Item {
    
    public MoeSpawnItem() {
        super(new Properties().group(MoeMod.ITEMS));
    }
    
    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (world.isRemote()) { return ActionResultType.PASS; }
        PlayerEntity player = context.getPlayer();
        BlockPos block = context.getPos();
        BlockPos spawn = block.offset(context.getFace());
        Dere dere = Dere.values()[world.rand.nextInt(Dere.values().length)];
        if (MoeEntity.spawn(world, block, spawn, player.rotationYaw, player.rotationPitch, dere, player)) {
            context.getItem().shrink(1);
            return ActionResultType.CONSUME;
        } else {
            player.sendStatusMessage(new TranslationTextComponent("command.moeblocks.spawn.error"), true);
            return ActionResultType.FAIL;
        }
    }
}
