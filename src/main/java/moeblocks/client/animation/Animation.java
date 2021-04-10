package moeblocks.client.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;

public enum Animation {
    DEFAULT, YEARBOOK;

    public void setRotationAngles(AbstractNPCEntity entity, IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks) {

    }

    public void render(AbstractNPCEntity entity, MatrixStack stack, float partialTickTime) {

    }
}
