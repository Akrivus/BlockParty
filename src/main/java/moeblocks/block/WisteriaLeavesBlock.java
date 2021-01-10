package moeblocks.block;

import moeblocks.init.MoeBlocks;
import moeblocks.init.MoeParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class WisteriaLeavesBlock extends LeavesBlock {
    public WisteriaLeavesBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (super.ticksRandomly(state)) {
            super.randomTick(state, world, pos, random);
        } else if (!state.get(PERSISTENT)) {
            if (world.isAirBlock(pos.down())) {
                world.setBlockState(pos.down(), MoeBlocks.WISTERIA_VINE_TIP.get().getDefaultState());
            }
        }
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return true;
    }
}
