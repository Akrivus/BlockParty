package mod.moeblocks.client.animation;

import mod.moeblocks.client.Animations;
import mod.moeblocks.client.model.MoeModel;
import mod.moeblocks.entity.MoeEntity;

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
