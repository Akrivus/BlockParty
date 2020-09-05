package moe.blocks.mod.entity.behavior;

import moe.blocks.mod.entity.util.Behaviors;
import net.minecraft.particles.ParticleTypes;

public class HoneyBlockBehavior extends BasicBehavior {
    @Override
    public void tick() {
        this.moe.world.addParticle(ParticleTypes.DRIPPING_HONEY, this.moe.getCenteredRandomPosX(), this.moe.getPosYRandom(), this.moe.getCenteredRandomPosZ(), 0.0D, 0.0D, 0.0D);
        super.tick();
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.HONEY_BLOCK;
    }
}
