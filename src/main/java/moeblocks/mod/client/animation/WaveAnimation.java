package moeblocks.mod.client.animation;

import moeblocks.mod.client.Animations;
import moeblocks.mod.client.model.MoeModel;
import moeblocks.mod.entity.MoeEntity;

public class WaveAnimation extends ActionAnimation {
    @Override
    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }

    @Override
    public Animations getKey() {
        return Animations.WAVE;
    }

    @Override
    public int getInterval() {
        return 20;
    }
}
