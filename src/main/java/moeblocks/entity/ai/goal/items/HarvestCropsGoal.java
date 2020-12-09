package moeblocks.entity.ai.goal.items;

import moeblocks.entity.AbstractNPCEntity;
import moeblocks.entity.ai.goal.AbstractMoveToBlockGoal;
import net.minecraft.block.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class HarvestCropsGoal extends AbstractMoveToBlockGoal<AbstractNPCEntity> {

    public HarvestCropsGoal(AbstractNPCEntity entity) {
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
        Block block = state.getBlock();
        if (block instanceof NetherWartBlock) { return state.get(NetherWartBlock.AGE) == 3; }
        if (block instanceof BeetrootBlock) { return state.get(BeetrootBlock.BEETROOT_AGE) == 3; }
        if (block instanceof CropsBlock) { return state.get(CropsBlock.AGE) == 7; }
        return block == Blocks.MELON || block == Blocks.PUMPKIN;
    }
}
