package block_party.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class SakuraBlossomsBlock extends LeavesBlock {
    public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");

    public SakuraBlossomsBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DISTANCE, DECAY_DISTANCE)
                .setValue(PERSISTENT, false)
                .setValue(WATERLOGGED, false)
                .setValue(BLOOMING, true));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (!state.getValue(PERSISTENT)) {
            this.bloomOrClose(state, pos, level);
        }
    }

    private void bloomOrClose(BlockState state, BlockPos pos, Level level) {
        boolean blooming = level.getMoonBrightness() == 1.0F;
        if (state.getValue(BLOOMING) != blooming) {
            level.setBlockAndUpdate(pos, state.setValue(BLOOMING, blooming));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, PERSISTENT, WATERLOGGED, BLOOMING);
    }
}
