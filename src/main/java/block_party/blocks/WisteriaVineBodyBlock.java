package block_party.blocks;

import block_party.registry.CustomBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WisteriaVineBodyBlock extends GrowingPlantBodyBlock {
    public static final MapCodec<WisteriaVineBodyBlock> CODEC = simpleCodec(WisteriaVineBodyBlock::new);
    public static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public WisteriaVineBodyBlock(Properties properties) {
        super(properties.lightLevel(state -> 2), Direction.DOWN, SHAPE, false);
    }

    @Override
    protected MapCodec<? extends GrowingPlantBodyBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
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
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) CustomBlocks.WISTERIA_VINE_TIP.get();
    }
}
