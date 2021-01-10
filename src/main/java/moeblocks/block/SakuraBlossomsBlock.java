package moeblocks.block;

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

public class SakuraBlossomsBlock extends LeavesBlock {
    public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");

    public SakuraBlossomsBlock(Properties properties) {
        super(properties.setAllowsSpawn((state, reader, pos, entity) -> false).setSuffocates((state, reader, pos) -> false).setBlocksVision((state, reader, pos) -> false));
        this.setDefaultState(this.stateContainer.getBaseState().with(DISTANCE, 7).with(PERSISTENT, false).with(BLOOMING, true));
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
        if (state.get(PERSISTENT)) { return; }
        this.bloomOrClose(state, pos, world);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (super.ticksRandomly(state)) {
            super.randomTick(state, world, pos, random);
        } else if (!state.get(PERSISTENT)) {
            this.bloomOrClose(state, pos, world);
        }
    }

    @Override
    public boolean ticksRandomly(BlockState state) {
        return true;
    }

    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
        super.animateTick(state, world, pos, random);
        if (random.nextInt(15) != 0) { return; }
        if (state.get(BLOOMING)) {
            BlockPos loc = pos.down();
            BlockState check = world.getBlockState(loc);
            if (check.isSolidSide(world, loc, Direction.UP)) { return; }
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + 0.05D;
            double z = pos.getZ() + random.nextDouble();
            world.addParticle(MoeParticles.SAKURA.get(), x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, PERSISTENT, BLOOMING);
    }

    private void bloomOrClose(BlockState state, BlockPos pos, World world) {
        if (world.getMoonFactor() == 1.0F) {
            world.setBlockState(pos, state.with(BLOOMING, true));
        } else if (state.get(BLOOMING)) {
            world.setBlockState(pos, state.with(BLOOMING, false));
        }
    }
}
