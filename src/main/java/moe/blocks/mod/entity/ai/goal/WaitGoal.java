package moe.blocks.mod.entity.ai.goal;

import moe.blocks.mod.entity.StudentEntity;
import moe.blocks.mod.util.SorterAffection;
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
    public boolean shouldContinueExecuting() {
        return this.entity.canBeTarget(this.target) && this.entity.isWithinHomeDistanceCurrentPosition();
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.isWaiting()) {
            if (this.entity.canBeTarget(this.entity.getFollowTarget())) {
                this.target = this.entity.getFollowTarget();
                return true;
            } else {
                List<LivingEntity> victims = this.entity.world.getEntitiesWithinAABB(LivingEntity.class, this.entity.getBoundingBox().grow(8.0F, 4.0F, 8.0F));
                victims.sort(new SorterAffection(this.entity));
                this.target = victims.isEmpty() ? null : victims.get(0);
                return !victims.isEmpty();
            }
        }
        return false;
    }

    @Override
    public void startExecuting() {
        if (this.entity.isWithinHomeDistanceCurrentPosition()) {
            if (this.entity.canBeTarget(this.target) && !this.entity.canSee(this.target)) {
                this.entity.getNavigator().tryMoveToEntityLiving(this.target, 0.5D);
                this.entity.setSneaking(true);
            } else {
                this.entity.setSneaking(false);
            }
        } else {
            BlockPos pos = this.entity.getHomePosition();
            this.entity.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0F);
            this.entity.setSprinting(false);
        }
    }

    @Override
    public void tick() {
        if (this.entity.canBeTarget(this.target) && this.entity.canSee(this.target)) {
            this.entity.setSneaking(false);
        }
    }
}
