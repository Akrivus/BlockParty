package block_party.blocks;

import block_party.client.particle.SakuraParticle;
import block_party.registry.CustomParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class GinkgoLeavesBlock extends LeavesBlock {
    public GinkgoLeavesBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        SakuraParticle.add(CustomParticles.GINKGO.get(), level, pos, random);
    }
}
