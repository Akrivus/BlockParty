package moeblocks.block;

import moeblocks.block.entity.ShimenawaTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ShimenawaBlock extends AbstractDataBlock<ShimenawaTileEntity> {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    public static final BooleanProperty HANGING = BooleanProperty.create("hanging");
    protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 12.0D, 12.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0D, 12.0D, 0.0D, 4.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 4.0D);
    protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(12.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public ShimenawaBlock(Properties properties) {
        super(ShimenawaTileEntity::new, properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HANGING, true));
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
        BlockPos base = pos.offset(state.get(HANGING) ? Direction.UP : state.get(FACING).getOpposite());
        if (world.isAirBlock(base)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.toRotation(state.get(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        switch (state.get(FACING)) {
        default:
        case NORTH:
            return state.get(HANGING) ? NORTH_AABB.withOffset(0.0, 0.0, -0.375) : NORTH_AABB;
        case EAST:
            return state.get(HANGING) ? EAST_AABB.withOffset(0.375, 0.0, 0.0) : EAST_AABB;
        case SOUTH:
            return state.get(HANGING) ? SOUTH_AABB.withOffset(0.0, 0.0, 0.375) : SOUTH_AABB;
        case WEST:
            return state.get(HANGING) ? WEST_AABB.withOffset(-0.375, 0.0, 0.0) : WEST_AABB;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState state = this.getDefaultState().with(HANGING, context.getFace() == Direction.DOWN);
        return state.with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, HANGING);
    }
}

