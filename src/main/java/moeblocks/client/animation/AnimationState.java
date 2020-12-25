package moeblocks.client.animation;

import moeblocks.automata.IState;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;

public abstract class AnimationState implements IState<AbstractNPCEntity> {
    public abstract void setRotationAngles(IRiggableModel model, AbstractNPCEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks);
    
    @Override
    public boolean canClear(AbstractNPCEntity applicant) {
        return !this.canApply(applicant);
    }
}
