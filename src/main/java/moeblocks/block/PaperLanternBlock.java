package moeblocks.block;

import moeblocks.block.entity.PaperLanternTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class PaperLanternBlock extends AbstractDataBlock<PaperLanternTileEntity> {
    protected static final VoxelShape AABB = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    private final MaterialColor color;

    public PaperLanternBlock(Properties properties, MaterialColor color) {
        super(PaperLanternTileEntity::new, properties.setLightLevel((state) -> 15));
        this.color = color;
    }

    @Override
    public MaterialColor getMaterialColor() {
        return this.color;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return AABB;
    }
}
