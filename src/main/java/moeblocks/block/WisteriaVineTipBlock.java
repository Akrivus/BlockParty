package moeblocks.block;

import moeblocks.init.MoeBlocks;
import moeblocks.init.MoeTags;
import net.minecraft.block.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import java.util.Random;

public class WisteriaVineTipBlock extends AbstractTopPlantBlock {
    public static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public WisteriaVineTipBlock(Properties properties) {
        super(properties.setLightLevel((state) -> 2), Direction.DOWN, SHAPE, false, 0.1F);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    protected int getGrowthAmount(Random rand) {
        return PlantBlockHelper.getGrowthAmount(rand);
    }

    @Override
    protected boolean canGrowIn(BlockState state) {
        return PlantBlockHelper.isAir(state);
    }

    @Override
    protected Block getBodyPlantBlock() {
        return MoeBlocks.WISTERIA_VINE_BODY.get();
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos up = pos.up();
        BlockState ceiling = worldIn.getBlockState(up);
        return ceiling.isIn(MoeTags.Blocks.WISTERIA_BLOCKS);
    }
}
