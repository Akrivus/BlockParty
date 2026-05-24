package block_party.blocks;

import block_party.client.particle.SakuraParticle;
import block_party.registry.CustomSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.particles.SimpleParticleType;
import java.util.List;
import java.util.function.Supplier;

public class SakuraBlossomsBlock extends LeavesBlock {
    public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");
    private final Supplier<SimpleParticleType> particle;

    public SakuraBlossomsBlock(Supplier<SimpleParticleType> particle, Properties properties) {
        super(properties);
        this.particle = particle;
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
            if (state.getValue(BLOOMING) && random.nextInt(2000) == 0) {
                List<ServerPlayer> players = level.getPlayers(player -> pos.distSqr(player.blockPosition()) < 50.0D);
                for (ServerPlayer player : players) {
                    player.playNotifySound(CustomSounds.AMBIENT_JAPAN.get(), SoundSource.AMBIENT, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (state.getValue(BLOOMING)) {
            SakuraParticle.add(this.particle.get(), level, pos, random);
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
