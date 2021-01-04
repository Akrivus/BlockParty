package moeblocks.block;

import moeblocks.init.MoeBlocks;
import moeblocks.init.MoeParticles;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Iterator;
import java.util.Random;

public class SakuraLeavesBlock extends LeavesBlock {
    public SakuraLeavesBlock(MaterialColor color) {
        super(AbstractBlock.Properties.create(Material.LEAVES, color).hardnessAndResistance(0.2F).tickRandomly().sound(SoundType.PLANT).notSolid());
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
        this.bloomOrClose(state, pos, world);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        if (state.get(PERSISTENT) || state.get(DISTANCE) < 7) {
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
        if (state.isIn(MoeBlocks.SAKURA_BLOSSOMS.get())) {
            BlockPos loc = pos.down();
            BlockState check = world.getBlockState(loc);
            if (check.isSolidSide(world, loc, Direction.UP)) { return; }
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + 0.05D;
            double z = pos.getZ() + random.nextDouble();
            world.addParticle(MoeParticles.SAKURA.get(), x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    private BlockState replace(BlockState state, Block block) {
        BlockState result = block.getDefaultState();
        Iterator<Property<?>> it = state.getProperties().iterator();
        while (it.hasNext()) {
            Property prop = it.next();
            result.with(prop, state.get(prop));
        }
        return result;
    }

    private void bloom(BlockState state, BlockPos pos, World world) {
        if (state.isIn(MoeBlocks.SAKURA_BLOSSOMS.get())) { return; }
        world.setBlockState(pos, this.replace(state, MoeBlocks.SAKURA_BLOSSOMS.get()));
    }

    private void close(BlockState state, BlockPos pos, World world) {
        if (state.isIn(MoeBlocks.SAKURA_LEAVES.get())) { return; }
        world.setBlockState(pos, this.replace(state, MoeBlocks.SAKURA_LEAVES.get()));
    }

    private void bloomOrClose(BlockState state, BlockPos pos, World world) {
        if (world.getMoonFactor() == 1.0F) {
            this.bloom(state, pos, world);
        } else {
            this.close(state, pos, world);
        }
    }
}
