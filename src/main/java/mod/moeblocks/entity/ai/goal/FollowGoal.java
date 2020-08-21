package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.StateEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class FollowGoal extends Goal {
    private final StateEntity entity;
    private int timeUntilMoveIn;

    public FollowGoal(StateEntity entity) {
        super();
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.canBeTarget(this.entity.getFollowTarget())) {
            float distance = this.entity.getFollowTarget().getDistance(this.entity);
            return distance > this.entity.getRelationships().get(this.entity.getFollowTarget()).getDistance();
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.canBeTarget(this.entity.getFollowTarget()) && this.entity.hasPath();
    }

    @Override
    public void startExecuting() {
        LivingEntity target = this.entity.getFollowTarget();
        BlockPos pos = target.getPosition();
        this.entity.turnToView(target);
        if (this.entity.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), this.entity.getFollowSpeed(target, 8.0F))) {
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
        float distance = this.entity.getFollowTarget().getDistance(this.entity);
        if (distance > this.entity.getRelationships().get(this.entity.getFollowTarget()).getDistance() || --this.timeUntilMoveIn < 0) {
            this.startExecuting();
        }
    }
}