package moe.blocks.mod.entity.goal;

import moe.blocks.mod.entity.FiniteEntity;
import moe.blocks.mod.init.MoeTags;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class MineGoal extends BreakGoal {

    public MineGoal(FiniteEntity entity) {
        super(entity, 3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.isMiner()) {
            return super.shouldExecute();
        }
        return false;
    }

    @Override
    protected boolean canBreakBlock(BlockPos pos, BlockState state) {
        return state.getBlock().isIn(MoeTags.ORES);
    }
}
