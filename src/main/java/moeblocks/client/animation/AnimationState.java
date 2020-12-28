package moeblocks.client.animation;

import moeblocks.automata.state.WatchedGoalState;
import moeblocks.automata.state.enums.Animation;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;

public abstract class AnimationState extends WatchedGoalState<Animation, AbstractNPCEntity> {
    public AnimationState(Animation filter) {
        super(filter, (npc, list) -> { }, AbstractNPCEntity.ANIMATION);
    }
    
    public abstract void setRotationAngles(IRiggableModel model, AbstractNPCEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks);
    
    @Override
    public boolean canClear(AbstractNPCEntity applicant) {
        return !this.canApply(applicant);
    }
}
