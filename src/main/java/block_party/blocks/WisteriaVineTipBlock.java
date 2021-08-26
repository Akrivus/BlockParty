package block_party.blocks;

import block_party.init.BlockPartyBlocks;
import block_party.init.BlockPartyTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class WisteriaVineTipBlock extends GrowingPlantHeadBlock {
    public static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public WisteriaVineTipBlock(Properties properties) {
        super(properties.lightLevel((state) -> 2), Direction.DOWN, SHAPE, false, 0.1F);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(Random rand) {
        return NetherVines.getBlocksToGrowWhenBonemealed(rand);
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return NetherVines.isValidGrowthState(state);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockPos up = pos.above();
        BlockState ceiling = worldIn.getBlockState(up);
        return ceiling.is(BlockPartyTags.Blocks.WISTERIA);
    }

    @Override
    protected Block getBodyBlock() {
        return BlockPartyBlocks.WISTERIA_VINE_BODY.get();
    }
}
