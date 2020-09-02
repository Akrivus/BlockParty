package moeblocks.mod.entity.ai.goal;

import moeblocks.mod.entity.StudentEntity;
import moeblocks.mod.util.DistanceCheck;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.List;

public class WaitGoal extends Goal {
    private final StudentEntity entity;
    private LivingEntity target;

    public WaitGoal(StudentEntity entity) {
        super();
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.isWaiting() && !this.entity.isWithinHomeDistanceCurrentPosition()) {
            List<LivingEntity> victims = this.entity.world.getEntitiesWithinAABB(LivingEntity.class, this.entity.getBoundingBox().grow(8.0F, 4.0F, 8.0F));
            victims.sort(new DistanceCheck(this.entity));
            this.target = victims.isEmpty() ? null : victims.get(0);
            return !victims.isEmpty();
        }
        return false;
    }

    @Override
    public void resetTask() {
        this.entity.setSneaking(false);
    }

    @Override
    public void startExecuting() {
        BlockPos pos = this.entity.getHomePosition();
        this.entity.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0F);
        this.entity.setSprinting(false);
        if (this.entity.canBeTarget(this.target) && !this.entity.canSee(this.target)) {
            this.entity.getNavigator().tryMoveToEntityLiving(this.target, 0.5D);
            this.entity.setSneaking(true);
        } else {
            this.entity.setSneaking(false);
        }
    }
}
