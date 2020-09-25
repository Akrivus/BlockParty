package moe.blocks.mod.entity.ai.goal.attack;

import moe.blocks.mod.entity.ai.goal.AbstractMoveToEntityGoal;
import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class BasicAttackGoal<E extends NPCEntity> extends AbstractMoveToEntityGoal<NPCEntity, LivingEntity> {

    public BasicAttackGoal(E entity) {
        super(entity, LivingEntity.class, 1.0);
    }

    @Override
    public int getPriority() {
        return 0x3;
    }

    @Override
    public void onArrival() {
        this.entity.attackEntityAsMob(this.target);
        this.entity.swingArm(Hand.MAIN_HAND);
    }

    @Override
    public float getStrikeZone(LivingEntity target) {
        return this.entity.getStrikingDistance(target);
    }

    @Override
    public float getSafeZone(LivingEntity target) {
        return 0.0F;
    }

    @Override
    public boolean canMoveTo(LivingEntity target) {
        return target.equals(this.entity.getAttackTarget());
    }
}
