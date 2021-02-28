package moeblocks.block;

import moeblocks.automata.state.enums.RibbonColor;
import moeblocks.automata.state.enums.TimeOfDay;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class PaperLanternBlock extends WaypointBlock {
    protected static final VoxelShape AABB = Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);

    public PaperLanternBlock(Properties properties, RibbonColor color, TimeOfDay time) {
        super(properties.setLightLevel((state) -> 15), color, time);
    }

    public PaperLanternBlock(Properties properties, RibbonColor color) {
        this(properties, color, null);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return AABB;
    }
}
