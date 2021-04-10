package moeblocks.block;

import moeblocks.init.MoeSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ShojiScreenBlock extends DoorBlock {
    public ShojiScreenBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return state.get(OPEN) ? VoxelShapes.empty() : this.getShape(state, world, pos, context);
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

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        world.setBlockState(pos, state = state.func_235896_a_(OPEN), 10);
        world.playSound(player, pos, (state.get(OPEN) ? MoeSounds.BLOCK_SHOJI_SCREEN_OPEN : MoeSounds.BLOCK_SHOJI_SCREEN_CLOSE).get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
        return ActionResultType.func_233537_a_(world.isRemote);
    }
}
