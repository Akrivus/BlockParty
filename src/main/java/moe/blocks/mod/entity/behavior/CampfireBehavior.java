package moe.blocks.mod.entity.behavior;

import moe.blocks.mod.entity.util.Behaviors;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;

public class CampfireBehavior extends BasicBehavior {
    @Override
    public void tick() {
        this.moe.world.getEntitiesWithinAABB(LivingEntity.class, this.moe.getBoundingBox()).forEach(entity -> {
            if (this.moe != entity && this.moe.canAttack(entity)) {
                entity.attackEntityFrom(DamageSource.IN_FIRE, this.getBlock() == Blocks.SOUL_CAMPFIRE ? 2.0F : 1.0F);
            }
        });
        if (this.moe.isBurning()) {
            this.moe.extinguish();
        }
        super.tick();
    }

    @Override
    public boolean onDamage(DamageSource source, float amount) {
        return source.isFireDamage();
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.CAMPFIRE;
    }
}
