package moe.blocks.mod.entity.ai.goal.items;

import moe.blocks.mod.entity.ai.goal.AbstractMoveToBlockGoal;
import moe.blocks.mod.entity.partial.NPCEntity;
import moe.blocks.mod.init.MoeTags;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MineOresGoal extends AbstractMoveToBlockGoal<NPCEntity> {

    public MineOresGoal(NPCEntity entity) {
        super(entity, 4, 8);
    }

    @Override
    protected boolean isHoldingCorrectItem(ItemStack stack) {
        return stack.getItem().isIn(MoeTags.MINER);
    }

    @Override
    public void onArrival() {
        if (this.world.destroyBlock(this.pos, true)) { this.entity.swingArm(Hand.MAIN_HAND); }
    }

    @Override
    public boolean canMoveTo(BlockPos pos, BlockState state) {
        if (!this.entity.getHeldItem(Hand.MAIN_HAND).getItem().canHarvestBlock(state)) { return false; }
        return state.getBlock().isIn(MoeTags.MINEABLES);
    }

    @Override
    public int getPriority() {
        return 0x7;
    }
}
