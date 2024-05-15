package block_party.blocks;

import block_party.client.particle.SakuraParticle;
import block_party.registry.CustomSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
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

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class SakuraBlossomsBlock extends LeavesBlock {
    public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");
    private final Supplier<SimpleParticleType> particle;

    public SakuraBlossomsBlock(Supplier<SimpleParticleType> particle, Properties properties) {
        super(properties.isValidSpawn((state, reader, pos, entity) -> false).isSuffocating((state, reader, pos) -> false).isViewBlocking((state, reader, pos) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, 7).setValue(PERSISTENT, false).setValue(BLOOMING, true));
        this.particle = particle;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
        if (state.getValue(PERSISTENT)) { return; }
        this.bloomOrClose(state, pos, level);
    }

    private void bloomOrClose(BlockState state, BlockPos pos, Level level) {
        if (level.getMoonBrightness() == 1.0F) {
            level.setBlockAndUpdate(pos, state.setValue(BLOOMING, true));
        } else if (state.getValue(BLOOMING)) {
            level.setBlockAndUpdate(pos, state.setValue(BLOOMING, false));
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (state.getValue(PERSISTENT)) { return; }
        this.bloomOrClose(state, pos, level);
        if (state.getValue(BLOOMING) && random.nextInt(2000) == 0) {
            List<ServerPlayer> players = level.getPlayers(player -> pos.distSqr(player.blockPosition()) < 50);
            for (ServerPlayer player : players)
                player.playNotifySound(CustomSounds.AMBIENT_JAPAN.get(), SoundSource.AMBIENT, 1.0F, 1.0F);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (state.getValue(BLOOMING)) {
            SakuraParticle.add(this.particle, level, pos, random);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, PERSISTENT, BLOOMING);
    }
}
