package mod.moeblocks.client.animation;

import mod.moeblocks.client.Animations;
import mod.moeblocks.client.model.MoeModel;
import mod.moeblocks.entity.MoeEntity;

public class DabAnimation extends ActionAnimation {
    @Override
    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }

    @Override
    public Animations getKey() {
        return Animations.DAB;
    }

    @Override
    public int getInterval() {
        return 20;
    }
}
