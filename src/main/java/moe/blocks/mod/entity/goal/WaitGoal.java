package moe.blocks.mod.entity.goal;

import moe.blocks.mod.entity.FiniteEntity;
import moe.blocks.mod.util.sort.SorterAffection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;
import java.util.List;

public class WaitGoal extends Goal {
    private final FiniteEntity entity;
    private LivingEntity target;

    public WaitGoal(FiniteEntity entity) {
        this.setMutexFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.isWaiting()) {
            List<LivingEntity> victims = this.entity.world.getEntitiesWithinAABB(LivingEntity.class, this.entity.getBoundingBox().grow(8.0F, 1.0F, 8.0F));
            victims.sort(new SorterAffection(this.entity));
            this.target = victims.isEmpty() ? null : victims.get(0);
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.entity.canBeTarget(this.target) && this.entity.isWithinHomeDistanceCurrentPosition();
    }

    @Override
    public void startExecuting() {
        BlockPos pos = this.entity.getHomePosition();
        this.entity.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0F);
        this.entity.setSprinting(false);
    }

    @Override
    public void tick() {
        this.entity.canSee(this.target);
    }
}
