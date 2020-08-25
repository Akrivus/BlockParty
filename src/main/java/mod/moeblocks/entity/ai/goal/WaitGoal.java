package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.StateEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class WaitGoal extends Goal {
    private final StateEntity entity;

    public WaitGoal(StateEntity entity) {
        super();
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        return this.entity.isWaiting() && this.entity.getAttackTarget() == null && !this.entity.isWithinHomeDistanceCurrentPosition();
    }

    @Override
    public void startExecuting() {
        BlockPos pos = this.entity.getHomePosition();
        this.entity.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0F);
        this.entity.setSprinting(false);
    }
}
