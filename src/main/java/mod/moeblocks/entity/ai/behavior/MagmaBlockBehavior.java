package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;
import net.minecraft.block.Blocks;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class MagmaBlockBehavior extends BasicRandomBehavior {
    @Override
    public void tick() {
        this.moe.world.addParticle(ParticleTypes.FLAME, this.moe.getPosXRandom(0.375D), this.moe.getPosYRandom(), this.moe.getPosZRandom(0.375D), 0.0D, 0.0D, 0.0D);
        super.tick();
    }

    @Override
    public void onRandomTick() {
        BlockPos pos = this.moe.getPosition().down();
        if (this.moe.world.getBlockState(pos).isFlammable(this.moe.world, pos, Direction.UP)) {
            this.moe.world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
        }
    }

    @Override
    public boolean isGlowing() {
        return true;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.MAGMA_BLOCK;
    }
}
