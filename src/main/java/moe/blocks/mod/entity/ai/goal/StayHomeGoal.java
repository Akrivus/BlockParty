package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.partial.InteractEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class StayHomeGoal extends Goal {
    private final InteractEntity entity;
    private Vector3d pos;

    public StayHomeGoal(InteractEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        if (!this.entity.canWander() && this.entity.isWithinHomeDistanceCurrentPosition()) { return false; }
        this.pos = RandomPositionGenerator.findRandomTargetBlockTowards(this.entity, 16, 3, Vector3d.copyCentered(this.entity.getHomePosition()));
        return this.pos != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.hasPath();
    }

    @Override
    public void startExecuting() {
        this.entity.getNavigator().tryMoveToXYZ(this.pos.getX(), this.pos.getY(), this.pos.getZ(), 0.6F);
        this.entity.setSprinting(false);
    }
}
