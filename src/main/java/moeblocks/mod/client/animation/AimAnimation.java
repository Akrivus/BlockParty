package moeblocks.mod.client.animation;

import moeblocks.mod.client.Animations;
import moeblocks.mod.client.model.MoeModel;
import moeblocks.mod.entity.MoeEntity;

public class AimAnimation extends Animation {

    @Override
    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
        model.getArmForSide(entity.getPrimaryHand().opposite()).rotateAngleX = -1.57F + model.head.rotateAngleX;
        model.getArmForSide(entity.getPrimaryHand().opposite()).rotateAngleY = 0.1F + model.head.rotateAngleY + 0.4F;
        model.getArmForSide(entity.getPrimaryHand()).rotateAngleX = -1.57F + model.head.rotateAngleX;
        model.getArmForSide(entity.getPrimaryHand()).rotateAngleY = -0.1F + model.head.rotateAngleY;
    }

    @Override
    public Animations getKey() {
        return Animations.AIM;
    }
}
