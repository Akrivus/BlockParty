package moeblocks.block;

import moeblocks.block.entity.ToriiTabletTileEntity;
import moeblocks.init.MoeBlocks;
import moeblocks.init.MoeTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.BlockStateMatcher;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ToriiTabletBlock extends Block implements ITileEntityProvider {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(2.0D, 0.0D, 10.0D, 14.0D, 14.0D, 16.0D);
    protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 2.0D, 6.0D, 14.0D, 14.0D);
    protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(2.0D, 0.0D, 0.0D, 14.0D, 14.0D, 6.0D);
    protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(10.0D, 0.0D, 2.0D, 16.0D, 14.0D, 14.0D);

    public ToriiTabletBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
        BlockPos behind = pos.offset(state.get(FACING).getOpposite());
        if (world.isAirBlock(behind)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockPattern.PatternHelper match = this.getGatePattern().match(world, pos);
        if (match != null) { this.getTileEntity(world, pos).claim((PlayerEntity) (placer)); }
    }

    public ToriiTabletTileEntity getTileEntity(World world, BlockPos pos) {
        return ((ToriiTabletTileEntity) (world.getTileEntity(pos)));
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
        return BlockPatternBuilder.start().aisle("#######", "###X###", " #   # ", " #   # ", " #   # ", " #   # ").where('#', CachedBlockInfo.hasState((state) -> state.isIn(MoeTags.Blocks.SAKURA_WOOD))).where('X', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(MoeBlocks.TORII_TABLET.get()))).where(' ', CachedBlockInfo.hasState((state) -> !state.isSolid())).build();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction facing = context.getPlacementHorizontalFacing();
        if (context.getWorld().getBlockState(context.getPos().offset(facing)).isIn(MoeTags.Blocks.SAKURA_WOOD)) {
            return this.getDefaultState().with(FACING, facing.getOpposite());
        } else {
            return null;
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader world) {
        return new ToriiTabletTileEntity();
    }
}
