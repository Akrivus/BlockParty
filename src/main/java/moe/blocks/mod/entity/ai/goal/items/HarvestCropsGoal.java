package moe.blocks.mod.entity.ai.goal.items;

import moe.blocks.mod.entity.ai.goal.AbstractMoveToBlockGoal;
import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.block.*;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class HarvestCropsGoal extends AbstractMoveToBlockGoal<NPCEntity> {

    public HarvestCropsGoal(NPCEntity entity) {
        super(entity, 4, 8);
    }

    @Override
    protected boolean isHoldingCorrectItem(ItemStack stack) {
        return stack.getItem() instanceof HoeItem;
    }

    @Override
    public void onArrival() {
        if (this.world.destroyBlock(this.pos, true)) { this.entity.swingArm(Hand.MAIN_HAND); }
    }

    @Override
    public boolean canMoveTo(BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof NetherWartBlock) { return state.get(NetherWartBlock.AGE) == 3; }
        if (block instanceof BeetrootBlock) { return state.get(BeetrootBlock.BEETROOT_AGE) == 3; }
        if (block instanceof CropsBlock) { return state.get(CropsBlock.AGE) == 7; }
        return block == Blocks.MELON || block == Blocks.PUMPKIN;
    }

    @Override
    public int getPriority() {
        return 0x7;
    }
}
