package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.HashMap;

public class CarpetBehavior extends BasicBehavior {
    protected static HashMap<Block, Block> CARPET_TO_WOOL = new HashMap<Block, Block>();

    static {
        CARPET_TO_WOOL.put(Blocks.BLACK_CARPET, Blocks.BLACK_WOOL);
        CARPET_TO_WOOL.put(Blocks.BLUE_CARPET, Blocks.BLUE_WOOL);
        CARPET_TO_WOOL.put(Blocks.BROWN_CARPET, Blocks.BROWN_WOOL);
        CARPET_TO_WOOL.put(Blocks.CYAN_CARPET, Blocks.CYAN_WOOL);
        CARPET_TO_WOOL.put(Blocks.GRAY_CARPET, Blocks.GRAY_WOOL);
        CARPET_TO_WOOL.put(Blocks.GREEN_CARPET, Blocks.GREEN_WOOL);
        CARPET_TO_WOOL.put(Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_BLUE_WOOL);
        CARPET_TO_WOOL.put(Blocks.LIGHT_GRAY_CARPET, Blocks.LIGHT_GRAY_WOOL);
        CARPET_TO_WOOL.put(Blocks.LIME_CARPET, Blocks.LIME_WOOL);
        CARPET_TO_WOOL.put(Blocks.MAGENTA_CARPET, Blocks.MAGENTA_WOOL);
        CARPET_TO_WOOL.put(Blocks.ORANGE_CARPET, Blocks.ORANGE_WOOL);
        CARPET_TO_WOOL.put(Blocks.PINK_CARPET, Blocks.PINK_WOOL);
        CARPET_TO_WOOL.put(Blocks.PURPLE_CARPET, Blocks.PURPLE_WOOL);
        CARPET_TO_WOOL.put(Blocks.RED_CARPET, Blocks.RED_WOOL);
        CARPET_TO_WOOL.put(Blocks.WHITE_CARPET, Blocks.WHITE_WOOL);
        CARPET_TO_WOOL.put(Blocks.YELLOW_CARPET, Blocks.YELLOW_WOOL);
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (CARPET_TO_WOOL.containsValue(Block.getBlockFromItem(stack.getItem()))) {
            this.moe.setBlockData(CARPET_TO_WOOL.get(Block.getBlockFromItem(stack.getItem())).getDefaultState());
            stack.shrink(1);
            return true;
        }
        return false;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.CARPET;
    }
}
