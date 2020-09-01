package moeblocks.mod.entity.ai.behavior;

import moeblocks.mod.entity.util.Behaviors;
import net.minecraft.block.Blocks;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;

public class MyceliumBehavior extends BasicRandomBehavior {
    @Override
    public void tick() {
        this.moe.world.addParticle(ParticleTypes.MYCELIUM, this.moe.getCenteredRandomPosX(), this.moe.getPosYRandom(), this.moe.getCenteredRandomPosZ(), 0.0D, 0.0D, 0.0D);
        super.tick();
    }

    @Override
    public void onRandomTick() {
        BlockPos pos = this.moe.getPosition().down();
        if (this.moe.world.getBlockState(pos).getBlock() == Blocks.DIRT) {
            this.moe.world.setBlockState(pos, this.moe.getBlockState());
        }
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.MYCELIUM;
    }
}
