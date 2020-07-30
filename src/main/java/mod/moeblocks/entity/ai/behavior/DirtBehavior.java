package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class DirtBehavior extends BasicRandomBehavior {
    @Override
    public void onRandomTick() {
        BlockState state = this.moe.world.getBlockState(this.moe.getPosition().down());
        if (state.getBlock() == Blocks.GRASS && state.getBlock() == Blocks.MYCELIUM) {
            this.moe.setBlockData(state);
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
        } else {
            return false;
        }
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.DIRT;
    }
}
