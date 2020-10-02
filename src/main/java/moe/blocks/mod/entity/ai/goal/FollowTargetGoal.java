package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.AbstractNPCEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.PathNodeType;

public class FollowTargetGoal extends AbstractFollowEntityGoal<AbstractNPCEntity, LivingEntity> {

    public FollowTargetGoal(AbstractNPCEntity entity) {
        super(entity, LivingEntity.class, 1.1D);
    }

    @Override
    public int getPriority() {
        return 0x7;
    }

    @Override
    public void resetTask() {
        this.entity.setPathPriority(PathNodeType.WATER, -1.0F);
        super.resetTask();
    }

    @Override
    public void onArrival() {
        if (this.target.isPassenger()) { this.entity.startRiding(this.target.getRidingEntity()); }
    }

    @Override
    public void onFollow() {
        if (this.entity.isPassenger() && this.target.isPassenger() != this.entity.isPassenger()) {
            this.entity.dismount();
        }
    }

    @Override
    public float getStrikeZone(LivingEntity target) {
        return 4.0F;
    }

    @Override
    public LivingEntity getTarget() {
        return this.entity.getFollowTarget();
    }

    @Override
    public boolean canFollow(LivingEntity target) {
        return true;
    }

    @Override
    public float getSafeZone(LivingEntity target) {
        return 4.0F;
    }
}