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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ToriiTabletBlock extends Block implements ITileEntityProvider {
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public ToriiTabletBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
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

    private BlockPattern getGatePattern() {
        return BlockPatternBuilder.start().aisle("#######", "###X###", " #   # ", " #   # ", " #   # ", " #   # ").where('#', CachedBlockInfo.hasState((state) -> state.isIn(MoeTags.Blocks.SAKURA_WOOD))).where('X', CachedBlockInfo.hasState(BlockStateMatcher.forBlock(MoeBlocks.TORII_TABLET.get()))).where(' ', CachedBlockInfo.hasState((state) -> !state.isSolid())).build();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
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
