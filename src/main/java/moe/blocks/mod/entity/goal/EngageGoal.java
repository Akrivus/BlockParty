package moe.blocks.mod.entity.goal;

import moe.blocks.mod.entity.FiniteEntity;
import net.minecraft.entity.LivingEntity;

public class EngageGoal<T extends LivingEntity> extends MoveGoal<T> {
    protected boolean engaging;
    private int timeUntilEngaging;
    private int timeUntilReset;

    public EngageGoal(FiniteEntity entity, Class<T> type) {
        super(entity, type, 0.75D);
    }

    @Override
    public boolean shouldExecute() {
        return --this.timeUntilEngaging < 0 && super.shouldExecute();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return super.shouldContinueExecuting() || this.engaging;
    }

    @Override
    public void startExecuting() {
        this.timeUntilReset = this.getResetDelay();
        this.engaging = true;
        super.startExecuting();
    }

    @Override
    public void resetTask() {
        this.timeUntilEngaging = this.getEngagementInterval();
        super.resetTask();
    }

    @Override
    public float getDistanceThreshhold() {
        return 3.0F;
    }

    @Override
    public void onFollowed() {
        this.entity.getNavigator().clearPath();
        if (--this.timeUntilReset < 0) {
            this.engage();
        }
    }

    public void engage() {

    }

    public int getEngagementInterval() {
        return 200;
    }

    public int getResetDelay() {
        return 20;
    }
}
