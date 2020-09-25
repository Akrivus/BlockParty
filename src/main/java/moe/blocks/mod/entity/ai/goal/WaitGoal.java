package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.partial.InteractEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class WaitGoal extends Goal {
    private final InteractEntity entity;

    public WaitGoal(InteractEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.isWithinHomeDistanceCurrentPosition()) { return false; }
        if (this.entity.isFollowing()) { return false; }
        return !this.entity.canBeTarget(this.entity.getAttackTarget());
    }

    @Override
    public void startExecuting() {
        BlockPos pos = this.entity.getHomePosition();
        this.entity.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0F);
        this.entity.setSprinting(false);
    }
}
