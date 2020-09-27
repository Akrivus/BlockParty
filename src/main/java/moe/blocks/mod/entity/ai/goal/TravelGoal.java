package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.partial.InteractEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class TravelGoal extends Goal {
    private final InteractEntity entity;
    private float offset = 1.0F;
    private boolean isReturningHome;
    private Vector3d pos;

    public TravelGoal(InteractEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.isFollowing() || this.entity.canBeTarget(this.entity.getAttackTarget())) { return false; }
        this.isReturningHome = 8 > this.entity.getHomeDistance() || this.entity.getHomeDistance() > this.entity.getMaximumHomeDistance();
        Vector3d target = this.isReturningHome ? Vector3d.copyCentered(this.entity.getHomePosition()) : this.getEdgeVector();
        this.pos = RandomPositionGenerator.findRandomTargetBlockTowards(this.entity, 16, 7, target);
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

    public Vector3d getEdgeVector() {
        double x = this.entity.getHomeDistance() * Math.cos(this.getRotationOffsetFromBloodType());
        double z = this.entity.getHomeDistance() * Math.sin(this.getRotationOffsetFromBloodType());
        return Vector3d.copy(this.entity.getPosition().add(x, 0, z));
    }

    public float getRotationOffsetFromBloodType() {
        return this.entity.getBloodType().ordinal() * this.offset * 1.5708F;
    }
}
