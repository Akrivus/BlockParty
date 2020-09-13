package moe.blocks.mod.client.animation.state;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.client.animation.Animation;
import moe.blocks.mod.client.model.MoeModel;
import moe.blocks.mod.entity.MoeEntity;
import net.minecraft.util.math.MathHelper;

public class Default extends Animation {
    @Override
    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }

    @Override
    public Animations getKey() {
        return Animations.DEFAULT;
    }
}
