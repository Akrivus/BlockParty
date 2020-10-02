package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.AbstractNPCEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.EnumSet;

public class WanderGoal extends Goal {
    private final AbstractNPCEntity entity;
    private Vector3d pos;

    public WanderGoal(AbstractNPCEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.ticksExisted % (this.entity.world.rand.nextInt(9) * 20 + 20) != 0) { return false; }
        if (!this.entity.canWander()) { return false; }
        this.pos = RandomPositionGenerator.findRandomTarget(this.entity, (int) this.entity.getHomeDistance(), 7);
        return this.pos != null && this.entity.isWithinHomeDistanceFromPosition(new BlockPos(this.pos));
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
