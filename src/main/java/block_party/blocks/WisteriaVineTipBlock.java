package block_party.blocks;

import block_party.registry.CustomBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class WisteriaVineTipBlock extends GrowingPlantHeadBlock {
    public static final MapCodec<WisteriaVineTipBlock> CODEC = simpleCodec(WisteriaVineTipBlock::new);
    public static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public WisteriaVineTipBlock(BlockBehaviour.Properties properties) {
        super(properties.lightLevel(state -> 2), Direction.DOWN, SHAPE, false, NetherVines.GROW_PER_TICK_PROBABILITY);
    }

    @Override
    protected MapCodec<? extends GrowingPlantHeadBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return NetherVines.getBlocksToGrowWhenBonemealed(random);
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return NetherVines.isValidGrowthState(state);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return above.is(CustomBlocks.WISTERIA_BINE.get())
                || above.is(CustomBlocks.WISTERIA_LEAVES.get())
                || above.is(CustomBlocks.WISTERIA_VINE_BODY.get())
                || above.is(CustomBlocks.WISTERIA_VINE_TIP.get());
    }

    @Override
    protected Block getBodyBlock() {
        return CustomBlocks.WISTERIA_VINE_BODY.get();
    }
}
