package block_party.blocks;

import block_party.blocks.entity.ShrineTabletBlockEntity;
import block_party.custom.CustomTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShrineTabletBlock extends AbstractDataBlock<ShrineTabletBlockEntity> {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    protected static final VoxelShape NORTH_AABB = Block.box(2.0D, 0.0D, 10.0D, 14.0D, 14.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 2.0D, 6.0D, 14.0D, 14.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(2.0D, 0.0D, 0.0D, 14.0D, 14.0D, 6.0D);
    protected static final VoxelShape WEST_AABB = Block.box(10.0D, 0.0D, 2.0D, 16.0D, 14.0D, 14.0D);

    public ShrineTabletBlock(Properties properties) {
        super(ShrineTabletBlockEntity::new, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
        BlockPos behind = pos.relative(state.getValue(FACING).getOpposite());
        if (level.isEmptyBlock(behind)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockPattern.BlockPatternMatch match = this.getGatePattern().find(level, pos.relative(state.getValue(FACING).getOpposite()));
        if (match != null) { super.setPlacedBy(level, pos, state, placer, stack); }
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

    private BlockPattern getGatePattern() {
        return BlockPatternBuilder.start().aisle("#######", "#######", " #   # ", " #   # ", " #   # ", " #   # ").where('#', BlockInWorld.hasState((state) -> state.is(CustomTags.Blocks.SAKURA_WOOD))).where(' ', BlockInWorld.hasState((state) -> !state.canOcclude())).build();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        if (context.getLevel().getBlockState(context.getClickedPos().relative(facing)).is(CustomTags.Blocks.SAKURA_WOOD)) {
            return this.defaultBlockState().setValue(FACING, facing.getOpposite());
        } else {
            return null;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShrineTabletBlockEntity(pos, state);
    }
}
