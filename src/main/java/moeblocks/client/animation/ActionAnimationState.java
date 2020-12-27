package moeblocks.client.animation;

import moeblocks.automata.state.keys.Animation;
import moeblocks.entity.AbstractNPCEntity;

public abstract class ActionAnimationState extends AnimationState {
    protected int timeUntilComplete;
    
    public ActionAnimationState(Animation filter) {
        super(filter);
    }
    
    @Override
    public void apply(AbstractNPCEntity applicant) {
        this.timeUntilComplete = this.getInterval();
    }
    
    @Override
    public boolean canApply(AbstractNPCEntity applicant) {
        return true;
    }
    
    @Override
    public void clear(AbstractNPCEntity applicant) {
    
    }
    
    public abstract int getInterval();
    
    @Override
    public boolean canClear(AbstractNPCEntity applicant) {
        return --this.timeUntilComplete < 0;
    }
}
