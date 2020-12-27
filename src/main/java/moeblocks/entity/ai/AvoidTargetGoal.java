package moeblocks.entity.ai;

import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class AvoidTargetGoal extends Goal {
    private final AbstractNPCEntity entity;
    private Path path;
    
    public AvoidTargetGoal(AbstractNPCEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.entity = entity;
    }
    
    @Override
    public boolean shouldExecute() {
        LivingEntity victim = this.entity.getAvoidTarget();
        if (this.entity.isAvoiding() && this.entity.canAttack(victim) && victim.getDistanceSq(this.entity) < 32.0D) {
            Vector3d path = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.entity, 16, 2, victim.getPositionVec());
            if (path == null || victim.getDistanceSq(path.x, path.y, path.z) < victim.getDistanceSq(this.entity)) {
                return false;
            }
            this.path = this.entity.getNavigator().getPathToPos(path.x, path.y, path.z, 0);
            return this.path != null;
        }
        return false;
    }
    
    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.hasPath() && this.entity.isAvoiding();
    }
    
    @Override
    public void startExecuting() {
        this.entity.getNavigator().setPath(this.path, 1.2F);
        this.entity.setSprinting(true);
    }
    
    @Override
    public void resetTask() {
        this.entity.setSprinting(false);
    }
}