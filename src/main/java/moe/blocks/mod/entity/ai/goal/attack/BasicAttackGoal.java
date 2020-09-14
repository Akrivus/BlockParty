package moe.blocks.mod.entity.ai.goal.attack;

import moe.blocks.mod.entity.ai.goal.AbstractFollowEntityGoal;
import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class BasicAttackGoal<E extends NPCEntity> extends AbstractFollowEntityGoal<NPCEntity, LivingEntity> {

    public BasicAttackGoal(E entity) {
        super(entity, LivingEntity.class, 1.0);
    }

    @Override
    public int getPriority() {
        return 0x6;
    }

    @Override
    public LivingEntity getTarget() {
        return this.entity.getAttackTarget();
    }

    @Override
    public boolean canFollow(LivingEntity target) {
        return this.entity.canAttack(target);
    }

    @Override
    public float getFollowDistance(LivingEntity target) {
        return (float) (Math.pow(this.entity.getWidth() * 2.0F, 2) + target.getWidth());
    }

    @Override
    public void onFollow() {

    }

    @Override
    public void onArrival() {
        if (this.entity.attackEntityAsMob(this.target)) { this.entity.swingArm(Hand.MAIN_HAND); }
    }
}
