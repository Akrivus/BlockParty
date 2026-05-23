package block_party.blocks;

import block_party.registry.CustomBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class WisteriaLeavesBlock extends LeavesBlock {
    public WisteriaLeavesBlock(Properties properties) {
        super(properties.lightLevel(state -> 2));
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (super.isRandomlyTicking(state)) {
            super.randomTick(state, level, pos, random);
        } else {
            growVineBelow(level, pos);
        }
    }

    public static boolean growVineBelow(ServerLevel level, BlockPos pos) {
        BlockPos below = pos.below();
        if (!level.isEmptyBlock(below)) {
            return false;
        }
        level.setBlockAndUpdate(below, CustomBlocks.WISTERIA_VINE_TIP.get().defaultBlockState());
        return true;
    }
}
