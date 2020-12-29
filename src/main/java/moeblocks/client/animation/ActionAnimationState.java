package moeblocks.client.animation;

import moeblocks.automata.state.enums.Animation;
import moeblocks.entity.AbstractNPCEntity;

public abstract class ActionAnimationState extends AnimationState {
    protected int timeUntilComplete;
    
    public ActionAnimationState(Animation filter) {
        super(filter);
    }
    
    @Override
    public void apply(AbstractNPCEntity applicant) {
        this.timeUntilComplete = this.getInterval();
        super.apply(applicant);
    }
    
    @Override
    public boolean canClear(AbstractNPCEntity applicant) {
        return --this.timeUntilComplete < 0;
    }
    
    public abstract int getInterval();
}
