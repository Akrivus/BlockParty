package moeblocks.client.animation.state;

import moeblocks.automata.state.enums.Animation;
import moeblocks.client.animation.AnimationState;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;
import net.minecraft.util.math.MathHelper;

public class Yearbook extends AnimationState {
    public Yearbook() {
        super(Animation.YEARBOOK);
    }
    
    @Override
    public void setRotationAngles(IRiggableModel model, AbstractNPCEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
        switch (entity.getDere()) {
            case HIMEDERE:
                model.getRightLeg().rotateAngleY = Math.abs(MathHelper.cos(ageInTicks * 0.125F)) * 0.5F;
                model.getLeftArm().rotateAngleZ = -(model.getRightArm().rotateAngleZ = 0.5F);
                break;
            case KUUDERE:
                model.getRightArm().rotateAngleX = model.getLeftArm().rotateAngleX = -0.6F;
                model.getLeftArm().rotateAngleZ = -(model.getRightArm().rotateAngleZ = -0.8F);
                break;
            case TSUNDERE:
                model.getLeftLeg().rotateAngleZ = -(model.getRightLeg().rotateAngleZ = 0.1F);
                model.getRightArm().rotateAngleX = model.getLeftArm().rotateAngleX = 0.0F;
                model.getLeftArm().rotateAngleZ = -(model.getRightArm().rotateAngleZ = 0.9F);
                break;
            case YANDERE:
                break;
            case DEREDERE:
                model.getBody().rotateAngleY = MathHelper.cos(ageInTicks * 0.125F) * 0.1F;
                model.getLeftLeg().rotateAngleZ = -(model.getRightLeg().rotateAngleZ = -0.1F);
                model.getRightArm().rotateAngleX = model.getLeftArm().rotateAngleX = 0.6F;
                model.getLeftArm().rotateAngleZ = -(model.getRightArm().rotateAngleZ = -0.8F);
                break;
            case DANDERE:
                model.getLeftLeg().rotateAngleZ = -(model.getRightLeg().rotateAngleZ = -0.1F);
                model.getRightArm().rotateAngleX = model.getLeftArm().rotateAngleX = -0.6F;
                model.getLeftArm().rotateAngleZ = -(model.getRightArm().rotateAngleZ = -0.8F);
                break;
        }
    }
}
