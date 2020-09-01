package moeblocks.mod.client.animation;

import moeblocks.mod.client.Animations;
import moeblocks.mod.entity.StateEntity;

public abstract class ActionAnimation extends Animation {
    protected int timeUntilComplete;

    public ActionAnimation() {
        this.setInterval();
    }

    public void setInterval() {
        this.timeUntilComplete = this.getInterval();
    }

    public int getInterval() {
        return 300;
    }

    @Override
    public void tick(StateEntity entity) {
        if (--this.timeUntilComplete < 0) {
            entity.setAnimation(Animations.DEFAULT);
            this.onComplete(entity);
        }
    }

    public void onComplete(StateEntity entity) {

    }
}
