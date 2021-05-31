package moeblocks.client.animation;

import com.mojang.blaze3d.matrix.MatrixStack;
import moeblocks.client.model.IRiggableModel;
import moeblocks.entity.AbstractNPCEntity;

public abstract class AbstractAnimation {
    public abstract void tick(AbstractNPCEntity entity);
    public abstract void setRotationAngles(AbstractNPCEntity entity, IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks);
    public abstract void render(AbstractNPCEntity entity, MatrixStack stack, float partialTickTime);
}
