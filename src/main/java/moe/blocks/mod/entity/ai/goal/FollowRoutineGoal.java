package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.AbstractNPCEntity;
import moe.blocks.mod.entity.ai.routines.Waypoint;
import net.minecraft.entity.ai.goal.Goal;

public class FollowRoutineGoal extends Goal {
    private final AbstractNPCEntity entity;
    private int timeUntilTravel;

    public FollowRoutineGoal(AbstractNPCEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        if (--this.timeUntilTravel > 0) { return false; }
        if (this.entity.isTimeToSleep()) {
            this.entity.setHomePosition(this.entity.getReturnPosition());
            this.entity.clearRoutine();
            this.timeUntilTravel = 24000;
            return false;
        } else {
            Waypoint waypoint = this.entity.getNextWaypoint();
            if (waypoint != null) {
                this.entity.setHomePosition(waypoint.getPosition());
                return true;
            } else {
                this.entity.setRoutine();
                return false;
            }
        }
    }

    @Override
    public void startExecuting() {
        this.timeUntilTravel = (int) (12000 - this.entity.world.getDayTime() % 12000) / (this.entity.getRoutine().size() + 1);
    }
}
