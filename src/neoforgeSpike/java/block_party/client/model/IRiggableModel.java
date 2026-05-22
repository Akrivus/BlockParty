package block_party.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

public interface IRiggableModel {
    default ModelPart getArmForSide(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.getLeftArm() : this.getRightArm();
    }

    ModelPart getRightArm();

    ModelPart getLeftArm();

    ModelPart getRightWing();

    ModelPart getLeftWing();

    ModelPart getRightLeg();

    ModelPart getLeftLeg();

    ModelPart getHead();

    ModelPart getHair();

    ModelPart getBody();

    ModelPart getTail();

    ModelPart getTailTip();
}
