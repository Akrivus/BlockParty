package moeblocks.client.animation.state;

import moeblocks.client.Animations;
import moeblocks.client.animation.ActionAnimation;
import moeblocks.client.model.MoeModel;
import moeblocks.entity.MoeEntity;
import net.minecraft.util.math.MathHelper;

public class FlapArms extends ActionAnimation {
    @Override
    public int getInterval() {
        return 20;
    }

    @Override
    public Animations getKey() {
        return Animations.FLAP_ARMS;
    }

    @Override
    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
        model.rightArm.rotateAngleZ -= 0.7853981633974483F * MathHelper.sin(ageInTicks);
        model.leftArm.rotateAngleZ -= -0.7853981633974483F * MathHelper.sin(ageInTicks);
        model.head.rotateAngleX = 0.2F;
        model.head.rotateAngleZ = 0.1F * MathHelper.sin(ageInTicks);
    }
}
