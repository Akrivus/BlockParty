package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.MoeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class FollowGoal extends Goal {
    private final MoeEntity moe;
    private int timeUntilMoveIn;

    public FollowGoal(MoeEntity moe) {
        super();
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.moe = moe;
    }

    @Override
    public boolean shouldExecute() {
        if (this.moe.canBeTarget(this.moe.getFollowTarget()) && !this.moe.isWaiting()) {
            float distance = this.moe.getFollowTarget().getDistance(this.moe);
            return distance > this.moe.getRelationships().get(this.moe.getFollowTarget()).getDistance();
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.moe.canBeTarget(this.moe.getFollowTarget()) && this.moe.hasPath();
    }

    @Override
    public void startExecuting() {
        LivingEntity target = this.moe.getFollowTarget();
        BlockPos pos = target.getPosition();
        if (this.moe.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), this.moe.getFollowSpeed(target, 8.0F))) {
            this.timeUntilMoveIn = this.moe.getNavigator().getPath().getCurrentPathLength() * 20 + 100;
        } else {
            this.timeUntilMoveIn = 200;
        }
    }

    @Override
    public void resetTask() {
        this.moe.getNavigator().clearPath();
        this.moe.setSprinting(false);
    }

    @Override
    public void tick() {
        float distance = this.moe.getFollowTarget().getDistance(this.moe);
        if (distance > this.moe.getRelationships().get(this.moe.getFollowTarget()).getDistance() || this.timeUntilMoveIn < 0) {
            this.startExecuting();
        } else {
            --this.timeUntilMoveIn;
        }
    }
}