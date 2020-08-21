package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;
import net.minecraft.particles.ParticleTypes;

public class SlimeBlockBehavior extends BasicBehavior {
    @Override
    public void tick() {
        this.moe.world.addParticle(ParticleTypes.ITEM_SLIME, this.moe.getCenteredRandomPosX(), this.moe.getPosYRandom(), this.moe.getCenteredRandomPosZ(), 0.0D, 0.0D, 0.0D);
        super.tick();
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.SLIME_BLOCK;
    }
}
