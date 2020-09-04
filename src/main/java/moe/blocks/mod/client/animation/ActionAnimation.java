package moe.blocks.mod.client.animation;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.entity.StudentEntity;

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
    public void tick(StudentEntity entity) {
        if (--this.timeUntilComplete < 0) {
            entity.setAnimation(Animations.DEFAULT);
            this.onComplete(entity);
        }
    }

    public void onComplete(StudentEntity entity) {

    }
}