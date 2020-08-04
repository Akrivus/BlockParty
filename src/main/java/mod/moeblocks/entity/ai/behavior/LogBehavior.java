package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;
import mod.moeblocks.entity.util.Emotions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.HashMap;

public class LogBehavior extends BasicBehavior {
    protected static HashMap<Block, Block> LOG_TO_STRIPPED = new HashMap<Block, Block>();

    static {
        LOG_TO_STRIPPED.put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG);
        LOG_TO_STRIPPED.put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD);
        LOG_TO_STRIPPED.put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG);
        LOG_TO_STRIPPED.put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD);
        LOG_TO_STRIPPED.put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG);
        LOG_TO_STRIPPED.put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD);
        LOG_TO_STRIPPED.put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG);
        LOG_TO_STRIPPED.put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD);
        LOG_TO_STRIPPED.put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG);
        LOG_TO_STRIPPED.put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD);
        LOG_TO_STRIPPED.put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG);
        LOG_TO_STRIPPED.put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD);
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (stack.getItem() instanceof AxeItem) {
            this.moe.setBlockData(LOG_TO_STRIPPED.get(this.moe.getBlockData().getBlock()).getDefaultState());
            this.moe.setEmotion(Emotions.ANGRY);
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
        return Behaviors.LOG;
    }
}
