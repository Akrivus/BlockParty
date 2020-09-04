package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.StudentEntity;
import net.minecraft.entity.LivingEntity;

public class FollowGoal extends MoveGoal<LivingEntity> {

    public FollowGoal(StudentEntity entity) {
        super(entity, LivingEntity.class, 1.0D);
    }

    @Override
    public boolean shouldExecute() {
        this.target = this.entity.getFollowTarget();
        if (this.entity.canBeTarget(this.target) && this.entity.getDistance(this.target) > this.getDistanceThreshhold()) {
            this.path = this.entity.getNavigator().getPathToEntity(this.target, 0);
            return this.path != null;
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.entity.isPassenger() && this.target.isPassenger() != this.entity.isPassenger()) {
            this.entity.dismount();
        }
    }

    @Override
    public float getDistanceThreshhold() {
        return this.entity.getRelationships().get(this.target).getDistance();
    }

    @Override
    public void onFollowed() {
        if (this.target.isPassenger()) {
            this.entity.startRiding(this.target.getRidingEntity());
        }
    }

    @Override
    public boolean canMoveTo(LivingEntity target) {
        return true;
    }
}