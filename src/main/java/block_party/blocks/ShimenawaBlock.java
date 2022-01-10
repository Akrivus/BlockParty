package block_party.blocks;

import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.registry.CustomTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShimenawaBlock extends AbstractDataBlock<ShimenawaBlockEntity> {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty HANGING = BooleanProperty.create("hanging");
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 12.0D, 12.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 12.0D, 0.0D, 4.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 4.0D);
    protected static final VoxelShape WEST_AABB = Block.box(12.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public ShimenawaBlock(Properties properties) {
        super(ShimenawaBlockEntity::new, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HANGING, true));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
        if (this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        BlockPos base = pos.relative(state.getValue(HANGING) ? Direction.UP : state.getValue(FACING).getOpposite());
        return reader.getBlockState(base).is(CustomTags.Blocks.SPAWNS_DOLLS);
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
            return state.getValue(HANGING) ? NORTH_AABB.move(0.0, 0.0, -0.375) : NORTH_AABB;
        case EAST:
            return state.getValue(HANGING) ? EAST_AABB.move(0.375, 0.0, 0.0) : EAST_AABB;
        case SOUTH:
            return state.getValue(HANGING) ? SOUTH_AABB.move(0.0, 0.0, 0.375) : SOUTH_AABB;
        case WEST:
            return state.getValue(HANGING) ? WEST_AABB.move(-0.375, 0.0, 0.0) : WEST_AABB;
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(HANGING, context.getClickedFace() == Direction.DOWN);
        return state.setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HANGING);
    }
}

