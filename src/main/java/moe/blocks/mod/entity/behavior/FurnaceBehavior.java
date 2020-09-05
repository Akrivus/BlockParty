package moe.blocks.mod.entity.behavior;

import moe.blocks.mod.entity.util.Behaviors;
import net.minecraft.block.Blocks;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class FurnaceBehavior extends BasicRandomBehavior {
    @Override
    public void tick() {
        this.moe.world.addParticle(ParticleTypes.FLAME, this.moe.getCenteredRandomPosX(), this.moe.getPosYRandom(), this.moe.getCenteredRandomPosZ(), 0.0D, 0.0D, 0.0D);
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
        return Behaviors.FURNACE;
    }
}
