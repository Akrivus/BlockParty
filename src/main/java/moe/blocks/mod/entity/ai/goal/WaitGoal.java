package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.partial.InteractiveEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class WaitGoal extends Goal {
    private final InteractiveEntity entity;
    private LivingEntity target;

    public WaitGoal(InteractiveEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return !this.entity.isFollowing();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.isWithinHomeDistanceCurrentPosition();
    }

    @Override
    public void startExecuting() {
        BlockPos pos = this.entity.getHomePosition();
        this.entity.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0F);
        this.entity.setSprinting(false);
    }
}
