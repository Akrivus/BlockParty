package moe.blocks.mod.entity.ai.behavior;

import moe.blocks.mod.entity.util.Behaviors;
import moe.blocks.mod.entity.util.Emotions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Hand;

import java.util.HashMap;

public class WoolBehavior extends BasicBehavior {
    protected static HashMap<Block, Block> WOOL_TO_CARPET = new HashMap<Block, Block>();

    static {
        WOOL_TO_CARPET.put(Blocks.BLACK_WOOL, Blocks.BLACK_CARPET);
        WOOL_TO_CARPET.put(Blocks.BLUE_WOOL, Blocks.BLUE_CARPET);
        WOOL_TO_CARPET.put(Blocks.BROWN_WOOL, Blocks.BROWN_CARPET);
        WOOL_TO_CARPET.put(Blocks.CYAN_WOOL, Blocks.CYAN_CARPET);
        WOOL_TO_CARPET.put(Blocks.GRAY_WOOL, Blocks.GRAY_CARPET);
        WOOL_TO_CARPET.put(Blocks.GREEN_WOOL, Blocks.GREEN_CARPET);
        WOOL_TO_CARPET.put(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_CARPET);
        WOOL_TO_CARPET.put(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_CARPET);
        WOOL_TO_CARPET.put(Blocks.LIME_WOOL, Blocks.LIME_CARPET);
        WOOL_TO_CARPET.put(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_CARPET);
        WOOL_TO_CARPET.put(Blocks.ORANGE_WOOL, Blocks.ORANGE_CARPET);
        WOOL_TO_CARPET.put(Blocks.PINK_WOOL, Blocks.PINK_CARPET);
        WOOL_TO_CARPET.put(Blocks.PURPLE_WOOL, Blocks.PURPLE_CARPET);
        WOOL_TO_CARPET.put(Blocks.RED_WOOL, Blocks.RED_CARPET);
        WOOL_TO_CARPET.put(Blocks.WHITE_WOOL, Blocks.WHITE_CARPET);
        WOOL_TO_CARPET.put(Blocks.YELLOW_WOOL, Blocks.YELLOW_CARPET);
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (stack.getItem() instanceof ShearsItem) {
            this.moe.entityDropItem(this.moe.getBlockData().getBlock());
            this.moe.setBlockData(WOOL_TO_CARPET.get(this.moe.getBlockData().getBlock()).getDefaultState());
            this.moe.setEmotion(Emotions.ANGRY, 1200);
            stack.damageItem(1, player, entity -> {
                entity.sendBreakAnimation(hand);
            });
            return true;
        }
        return false;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.WOOL;
    }
}
