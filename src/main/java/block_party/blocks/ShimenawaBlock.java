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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShimenawaBlock extends AbstractDataBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
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
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean isMoving) {
        if (!this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos base = pos.relative(state.getValue(HANGING) ? Direction.UP : state.getValue(FACING).getOpposite());
        return level.getBlockState(base).is(CustomTags.SPAWNS_MOES);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> state.getValue(HANGING) ? EAST_AABB.move(0.375D, 0.0D, 0.0D) : EAST_AABB;
            case SOUTH -> state.getValue(HANGING) ? SOUTH_AABB.move(0.0D, 0.0D, 0.375D) : SOUTH_AABB;
            case WEST -> state.getValue(HANGING) ? WEST_AABB.move(-0.375D, 0.0D, 0.0D) : WEST_AABB;
            default -> state.getValue(HANGING) ? NORTH_AABB.move(0.0D, 0.0D, -0.375D) : NORTH_AABB;
        };
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
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
