package moeblocks.client.animation.state;

import moeblocks.client.Animations;
import moeblocks.client.animation.Animation;
import moeblocks.client.model.MoeModel;
import moeblocks.entity.MoeEntity;

public class Default extends Animation {
    @Override
    public Animations getKey() {
        return Animations.DEFAULT;
    }

    @Override
    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }
}
