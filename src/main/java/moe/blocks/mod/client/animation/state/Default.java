package moe.blocks.mod.client.animation.state;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.client.animation.Animation;
import moe.blocks.mod.client.model.MoeModel;
import moe.blocks.mod.entity.MoeEntity;

public class Default extends Animation {
    @Override
    public Animations getKey() {
        return Animations.DEFAULT;
    }

    @Override
    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }
}
