package block_party.blocks;

import block_party.registry.CustomBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class WisteriaLeavesBlock extends LeavesBlock {
    public WisteriaLeavesBlock(Properties properties) {
        super(properties.lightLevel((state) -> 2).isValidSpawn((state, reader, pos, entity) -> false).isSuffocating((state, reader, pos) -> false).isViewBlocking((state, reader, pos) -> false));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random) {
        if (super.isRandomlyTicking(state)) {
            super.randomTick(state, level, pos, random);
        } else if (level.isEmptyBlock(pos.below())) {
            level.setBlockAndUpdate(pos.below(), CustomBlocks.WISTERIA_VINE_TIP.get().defaultBlockState());
        }
    }
}
