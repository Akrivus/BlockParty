package moeblocks.client.animation.state;

import moeblocks.automata.state.keys.Animation;
import moeblocks.client.animation.ActionAnimationState;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.util.math.MathHelper;

public class HappyDance extends ActionAnimationState {
    public HappyDance() {
        super(Animation.HAPPY_DANCE);
    }
    
    @Override
    public int getInterval() {
        return 20;
    }
    
    @Override
    public void setRotationAngles(IRiggableModel model, AbstractNPCEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
        model.getRightArm().rotateAngleZ -= 0.7853981633974483F * MathHelper.sin(ageInTicks);
        model.getLeftArm().rotateAngleZ -= -0.7853981633974483F * MathHelper.sin(ageInTicks);
        model.getHead().rotateAngleX = 0.2F;
        model.getHead().rotateAngleZ = 0.1F * MathHelper.sin(ageInTicks);
    }
}
