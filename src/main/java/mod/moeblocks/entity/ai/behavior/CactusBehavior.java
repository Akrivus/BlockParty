package mod.moeblocks.entity.ai.behavior;

import mod.moeblocks.entity.util.Behaviors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public class CactusBehavior extends BasicBehavior {
    @Override
    public void tick() {
        this.moe.world.getEntitiesWithinAABB(LivingEntity.class, this.moe.getBoundingBox()).forEach(entity -> entity.attackEntityFrom(DamageSource.CACTUS, 1.0F));
        super.tick();
    }

    @Override
    public boolean onDamage(DamageSource source, float amount) {
        return source == DamageSource.CACTUS;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.CACTUS;
    }
}
