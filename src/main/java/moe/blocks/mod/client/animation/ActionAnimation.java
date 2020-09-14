package moe.blocks.mod.client.animation;

import moe.blocks.mod.entity.partial.InteractEntity;

public abstract class ActionAnimation extends Animation {
    protected int timeUntilComplete;

    public ActionAnimation() {
        this.setInterval();
    }

    public void setInterval() {
        this.timeUntilComplete = this.getInterval();
    }

    public abstract int getInterval();

    @Override
    public void tick(InteractEntity entity) {
        if (--this.timeUntilComplete < 0) {
            entity.resetAnimationState();
            this.onComplete(entity);
        }
    }

    public void onComplete(InteractEntity entity) {

    }
}
