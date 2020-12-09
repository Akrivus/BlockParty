package moeblocks.client.animation.state;

import moeblocks.client.Animations;
import moeblocks.client.animation.ActionAnimation;
import moeblocks.client.model.MoeModel;
import moeblocks.entity.MoeEntity;

public class Wave extends ActionAnimation {
    @Override
    public int getInterval() {
        return 20;
    }

    @Override
    public Animations getKey() {
        return Animations.WAVE;
    }

    @Override
    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }
}
