package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.MoeEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class SitGoal extends Goal {
    private final MoeEntity moe;

    public SitGoal(MoeEntity moe) {
        super();
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
        this.moe = moe;
    }

    @Override
    public boolean shouldExecute() {
        return this.moe.isWaiting() && this.moe.getAttackTarget() == null && !this.moe.isWithinHomeDistanceCurrentPosition();
    }

    @Override
    public void startExecuting() {
        BlockPos pos = this.moe.getHomePosition();
        this.moe.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0F);
        this.moe.setSprinting(false);
    }
}
