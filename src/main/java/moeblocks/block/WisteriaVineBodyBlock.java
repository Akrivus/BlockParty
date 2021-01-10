package moeblocks.block;

import moeblocks.init.MoeBlocks;
import moeblocks.init.MoeTags;
import net.minecraft.block.AbstractBodyPlantBlock;
import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class WisteriaVineBodyBlock extends AbstractBodyPlantBlock {
    public static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public WisteriaVineBodyBlock(Properties properties) {
        super(properties.setLightLevel((state) -> 2), Direction.DOWN, SHAPE, false);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    protected AbstractTopPlantBlock getTopPlantBlock() {
        return (AbstractTopPlantBlock) MoeBlocks.WISTERIA_VINE_TIP.get();
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos up = pos.up();
        BlockState ceiling = worldIn.getBlockState(up);
        return ceiling.isIn(MoeTags.Blocks.WISTERIA_BLOCKS);
    }
}
