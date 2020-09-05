package moe.blocks.mod.entity.behavior;

import moe.blocks.mod.entity.util.Behaviors;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class GrassBlockBehavior extends BasicRandomBehavior {
    @Override
    public void onRandomTick() {
        BlockPos pos = this.moe.getPosition();
        if (this.moe.world.getBlockState(pos.down()).getBlock() == Blocks.DIRT) {
            this.moe.world.setBlockState(pos.down(), this.moe.getBlockState());
        } else if (this.moe.world.isDaytime() && this.moe.world.getLight(pos) < 9) {
            this.moe.setBlockData(Blocks.DIRT.getDefaultState());
        }
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (stack.getItem() instanceof HoeItem) {
            this.moe.setBlockData(Blocks.FARMLAND.getDefaultState());
            stack.damageItem(1, player, entity -> {
                entity.sendBreakAnimation(hand);
            });
            return true;
        } else if (stack.getItem() instanceof ShovelItem) {
            this.moe.setBlockData(Blocks.GRASS_PATH.getDefaultState());
            stack.damageItem(1, player, entity -> {
                entity.sendBreakAnimation(hand);
            });
            return true;
        }
        return false;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.GRASS_BLOCK;
    }
}
