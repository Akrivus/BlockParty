package moeblocks.client.animation.state;

import moeblocks.client.animation.AnimationState;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;

public class AimBow extends AnimationState {
    @Override
    public void apply(AbstractNPCEntity applicant) {
    
    }
    
    @Override
    public boolean canApply(AbstractNPCEntity applicant) {
        return false;
    }
    
    @Override
    public void clear(AbstractNPCEntity applicant) {
    
    }
    
    @Override
    public void setRotationAngles(IRiggableModel model, AbstractNPCEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
        model.getArmForSide(entity.getPrimaryHand().opposite()).rotateAngleX = -1.57F + model.getHead().rotateAngleX;
        model.getArmForSide(entity.getPrimaryHand().opposite()).rotateAngleY = 0.1F + model.getHead().rotateAngleY + 0.4F;
        model.getArmForSide(entity.getPrimaryHand()).rotateAngleX = -1.57F + model.getHead().rotateAngleX;
        model.getArmForSide(entity.getPrimaryHand()).rotateAngleY = -0.1F + model.getHead().rotateAngleY;
    }
}
