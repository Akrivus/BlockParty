package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.StateEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class FollowGoal extends Goal {
    private final StateEntity entity;
    private int timeUntilMoveIn;

    public FollowGoal(StateEntity entity) {
        super();
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity target = this.entity.getFollowTarget();
        if (this.entity.canBeTarget(target)) {
            return this.entity.getDistance(target) > this.entity.getRelationships().get(target).getDistance() || --this.timeUntilMoveIn < 0;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return --this.timeUntilMoveIn > 0 && this.entity.hasPath() && this.entity.canBeTarget(this.entity.getFollowTarget()) && this.entity.canSee(this.entity.getFollowTarget());
    }

    @Override
    public void startExecuting() {
        LivingEntity target = this.entity.getFollowTarget();
        if (this.entity.getNavigator().tryMoveToEntityLiving(target, this.entity.getFollowSpeed(target, 16.0F)) && this.entity.canSee(target)) {
            this.timeUntilMoveIn = this.entity.getNavigator().getPath().getCurrentPathLength() * 20 + 100;
        } else {
            this.timeUntilMoveIn = 200;
        }
    }

    @Override
    public void resetTask() {
        this.entity.getNavigator().clearPath();
        this.entity.setSprinting(false);
    }

    @Override
    public void tick() {
        LivingEntity target = this.entity.getFollowTarget();
        if (target.isPassenger()) {
            Entity steed = target.getRidingEntity();
            this.entity.startRiding(steed);
        } else if (this.entity.isPassenger()) {
            this.entity.dismount();
        }
    }
}