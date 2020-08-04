package mod.moeblocks.entity.ai.goal;

import mod.moeblocks.entity.MoeEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class LookGoal extends Goal {
    private final MoeEntity moe;
    private LivingEntity entity;

    public LookGoal(MoeEntity moe) {
        super();
        this.setMutexFlags(EnumSet.of(Flag.LOOK));
        this.moe = moe;
    }

    @Override
    public boolean shouldExecute() {
        if (this.moe.getFollowTarget() != null) {
            this.entity = this.moe.getFollowTarget();
            return true;
        } else if (this.moe.getAttackTarget() != null) {
            this.entity = this.moe.getAttackTarget();
            return true;
        } else if (this.moe.ticksExisted % 20 == 0 && this.moe.world.rand.nextBoolean()) {
            List<LivingEntity> victims = this.moe.world.getEntitiesWithinAABB(LivingEntity.class, this.moe.getBoundingBox().grow(8.0F, 4.0F, 8.0F));
            Collections.shuffle(victims);
            if (victims.isEmpty()) {
                return false;
            } else {
                this.entity = victims.get(0);
                return this.entity != null;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute() && this.moe.canBeTarget(this.entity);
    }

    @Override
    public void resetTask() {
        this.entity = null;
    }

    @Override
    public void tick() {
        this.moe.getLookController().setLookPositionWithEntity(this.entity, 30.0F, 30.0F);
    }
}