package moeblocks.block;

import moeblocks.init.MoeParticles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;

import java.util.Random;

public class SakuraBlossomsBlock extends LeavesBlock {
    public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");
    private final RegistryObject<BasicParticleType> particle;

    public SakuraBlossomsBlock(RegistryObject<BasicParticleType> particle, Properties properties) {
        super(properties.setAllowsSpawn((state, reader, pos, entity) -> false).setSuffocates((state, reader, pos) -> false).setBlocksVision((state, reader, pos) -> false));
        this.setDefaultState(this.stateContainer.getBaseState().with(DISTANCE, 7).with(PERSISTENT, false).with(BLOOMING, true));
        this.particle = particle;
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
        BlockPos spawn = pos.add(random.nextDouble(), -1.0D, random.nextDouble());
        if (random.nextInt(10) == 0 && state.get(BLOOMING) && world.isAirBlock(spawn)) {
            double direction = world.getDayTime() / 1000 * 15.0D;
            double x = Math.sin(0.0174444444D * direction) * (random.nextDouble() + random.nextInt(6));
            double z = Math.cos(0.0174444444D * direction) * (random.nextDouble() + random.nextInt(6));
            double y = Math.abs(random.nextGaussian()) * -1.0D;
            double start = spawn.getY() + 0.75F;
            world.addParticle(this.particle.get(), spawn.getX(), start, spawn.getZ(), x, y, z);
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
