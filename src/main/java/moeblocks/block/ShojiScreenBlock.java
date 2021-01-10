package moeblocks.block;

import moeblocks.init.MoeSounds;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.FakePlayerFactory;

public class ShojiScreenBlock extends DoorBlock {
    public ShojiScreenBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        world.setBlockState(pos, state = state.func_235896_a_(OPEN), 10);
        world.playSound(player, pos, (state.get(OPEN) ? MoeSounds.BLOCK_SHOJI_SCREEN_OPEN : MoeSounds.BLOCK_SHOJI_SCREEN_CLOSE).get(), SoundCategory.BLOCKS, 1.0F, 1.0F);
        return ActionResultType.func_233537_a_(world.isRemote);
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
    public VoxelShape getCollisionShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return state.get(OPEN) ? VoxelShapes.empty() : this.getShape(state, world, pos, context);
    }
}
