package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.partial.InteractiveEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.PathNodeType;

public class FollowTargetGoal extends AbstractFollowEntityGoal<InteractiveEntity, LivingEntity> {

    public FollowTargetGoal(InteractiveEntity entity) {
        super(entity, LivingEntity.class, 1.1D);
    }

    @Override
    public void resetTask() {
        this.entity.setPathPriority(PathNodeType.WATER, -1.0F);
        super.resetTask();
    }

    @Override
    public void onFollow() {
        if (this.entity.isPassenger() && this.target.isPassenger() != this.entity.isPassenger()) {
            this.entity.dismount();
        }
    }

    @Override
    public void onArrival() {
        if (this.target.isPassenger()) {
            this.entity.startRiding(this.target.getRidingEntity());
        }
    }

    @Override
    public float getReachDistance() {
        return super.getReachDistance() * 4.0F;
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
    public int getPriority() {
        return 0x7;
    }
}