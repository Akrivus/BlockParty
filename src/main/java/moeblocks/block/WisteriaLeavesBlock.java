package moeblocks.block;

import moeblocks.init.MoeBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class WisteriaLeavesBlock extends LeavesBlock {
    public WisteriaLeavesBlock(Properties properties) {
        super(properties.setLightLevel((state) -> 2).setAllowsSpawn((state, reader, pos, entity) -> false).setSuffocates((state, reader, pos) -> false).setBlocksVision((state, reader, pos) -> false));
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (super.ticksRandomly(state)) {
            super.randomTick(state, world, pos, random);
        } else if (world.isAirBlock(pos.down())) {
            world.setBlockState(pos.down(), MoeBlocks.WISTERIA_VINE_TIP.get().getDefaultState());
        }
    }
}
