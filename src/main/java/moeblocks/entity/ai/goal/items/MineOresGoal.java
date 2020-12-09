package moeblocks.entity.ai.goal.items;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.ai.goal.AbstractMoveToBlockGoal;
import moeblocks.init.MoeTags;
import net.minecraft.block.BlockState;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MineOresGoal extends AbstractMoveToBlockGoal<AbstractNPCEntity> {

    public MineOresGoal(AbstractNPCEntity entity) {
        super(entity, 7, 16);
        this.timeUntilNextMove = 20;
    }

    @Override
    public int getPriority() {
        return 0x7;
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
}
