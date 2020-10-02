package moe.blocks.mod.client.animation.state;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.client.animation.ActionAnimation;
import moe.blocks.mod.client.model.MoeModel;
import moe.blocks.mod.entity.MoeEntity;

public class Wave extends ActionAnimation {
    @Override
    public int getInterval() {
        return 20;
    }

    @Override
    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }

    @Override
    public Animations getKey() {
        return Animations.WAVE;
    }
}
