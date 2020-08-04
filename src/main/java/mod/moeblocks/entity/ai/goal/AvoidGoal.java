package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.MoeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class AvoidGoal extends Goal {
    private final MoeEntity moe;
    private Path path;

    public AvoidGoal(MoeEntity moe) {
        super();
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.moe = moe;
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity victim = this.moe.getAvoidTarget();
        if (this.moe.canBeTarget(victim) && this.moe.ticksExisted - this.moe.getAvoidTimer() < 600 && victim.getDistanceSq(this.moe) < 32) {
            Vec3d path = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.moe, 16, 7, victim.getPositionVec());
            if (path == null || victim.getDistanceSq(path.x, path.y, path.z) < victim.getDistanceSq(this.moe)) {
                return false;
            } else {
                this.path = this.moe.getNavigator().getPathToPos(path.x, path.y, path.z, 0);
                return this.path != null;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.moe.hasPath();
    }

    @Override
    public void startExecuting() {
        this.moe.getNavigator().setPath(this.path, this.getSpeed());
    }

    public float getSpeed() {
        float speed = 8.0F / this.moe.getDistance(this.moe.getAvoidTarget());
        this.moe.setSprinting(speed > 1.5F);
        return Math.min(speed, 2.0F);
    }

    @Override
    public void resetTask() {
        this.moe.setSprinting(false);
    }
}