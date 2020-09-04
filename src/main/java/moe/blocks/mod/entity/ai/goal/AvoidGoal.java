package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.StudentEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class AvoidGoal extends Goal {
    private final StudentEntity entity;
    private Path path;

    public AvoidGoal(StudentEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        LivingEntity victim = this.entity.getAvoidTarget();
        if (this.entity.canAttack(victim) && this.entity.isAvoiding() && victim.getDistanceSq(this.entity) < 32) {
            Vector3d path = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 7, victim.getPositionVec());
            if (path == null || victim.getDistanceSq(path.x, path.y, path.z) < victim.getDistanceSq(this.entity)) {
                return false;
            } else {
                this.path = this.entity.getNavigator().getPathToPos(path.x, path.y, path.z, 0);
                return this.path != null;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.hasPath() && this.entity.isAvoiding();
    }

    @Override
    public void startExecuting() {
        this.entity.getNavigator().setPath(this.path, this.getSpeed());
    }

    public float getSpeed() {
        float speed = 16.0F / this.entity.getDistance(this.entity.getAvoidTarget());
        this.entity.setSprinting(speed > 1.5F);
        return Math.min(speed, 2.0F);
    }

    @Override
    public void resetTask() {
        this.entity.setSprinting(false);
    }
}