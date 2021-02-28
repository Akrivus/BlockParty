package moeblocks.block;

import moeblocks.automata.state.enums.RibbonColor;
import moeblocks.automata.state.enums.TimeOfDay;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class ShojiLanternBlock extends PaperLanternBlock {
    public ShojiLanternBlock(Properties properties) {
        super(properties, RibbonColor.NONE);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.fullCube();
    }
}
