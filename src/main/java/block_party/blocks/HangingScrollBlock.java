package block_party.blocks;

import block_party.blocks.entity.HangingScrollBlockEntity;
import block_party.scene.SceneObservation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HangingScrollBlock extends AbstractDataBlock<HangingScrollBlockEntity> {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    protected static final VoxelShape NORTH_AABB = Block.box(1.0D, 1.0D, 0.0D, 15.0D, 15.0D, 2.0D);
    protected static final VoxelShape EAST_AABB = Block.box(14.0D, 1.0D, 1.0D, 16.0D, 15.0D, 15.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(1.0D, 1.0D, 14.0D, 15.0D, 15.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(0.0D, 1.0D, 1.0D, 2.0D, 15.0D, 15.0D);
    protected final SceneObservation condition;

    public HangingScrollBlock(Properties properties, SceneObservation condition) {
        super(HangingScrollBlockEntity::new, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        this.condition = condition;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
        BlockPos behind = pos.relative(state.getValue(FACING));
        if (level.isEmptyBlock(behind)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
        default:
        case NORTH:
            return NORTH_AABB;
        case EAST:
            return EAST_AABB;
        case SOUTH:
            return SOUTH_AABB;
        case WEST:
            return WEST_AABB;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace().getAxis().isVertical()) { return null; }
        return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}

