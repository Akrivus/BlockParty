package moeblocks.block;

import moeblocks.block.entity.WindChimesTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class WindChimesBlock extends AbstractDataBlock<WindChimesTileEntity> {
    protected static final VoxelShape AABB = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);

    public WindChimesBlock(Properties properties) {
        super(WindChimesTileEntity::new, properties.setLightLevel((state) -> 15));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return AABB;
    }
}
