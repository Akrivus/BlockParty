package block_party.client.animation;

import block_party.client.model.IRiggableModel;
import block_party.mob.Partyer;
import com.mojang.blaze3d.vertex.PoseStack;

public abstract class AbstractAnimation {
    public abstract void tick(Partyer entity);
    public abstract void setRotationAngles(Partyer entity, IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks);
    public abstract void render(Partyer entity, PoseStack stack, float partialTickTime);
}
