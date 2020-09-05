package moe.blocks.mod.entity.behavior;

import moe.blocks.mod.entity.util.Behaviors;
import moe.blocks.mod.entity.util.Emotions;
import net.minecraft.util.DamageSource;
import net.minecraft.world.Explosion;

public class TNTBehavior extends BasicBehavior {
    @Override
    public void tick() {
        if (this.moe.isLocal() && (this.moe.getEmotion().getKey() == Emotions.ANGRY || this.moe.isBurning())) {
            this.moe.attackEntityFrom(DamageSource.causeExplosionDamage(this.moe.world.createExplosion(this.moe, this.moe.getPosX(), this.moe.getPosYHeight(0.0625D), this.moe.getPosZ(), 4.0F, Explosion.Mode.BREAK)), Float.MAX_VALUE);
        }
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.TNT;
    }
}
