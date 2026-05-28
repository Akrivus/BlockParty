package block_party.client.animation.state;

import block_party.client.animation.AbstractAnimation;
import block_party.client.model.IRiggableModel;
import block_party.client.renderers.state.MoeRenderState;
import net.minecraft.util.Mth;

public class ShiverAnimation extends AbstractAnimation {
    @Override
    public void setRotationAngles(MoeRenderState state, IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float shake = Mth.sin(ageInTicks * 2.8F);
        float smallShake = Mth.sin(ageInTicks * 4.1F);
        model.getBody().zRot += shake * 0.04F;
        model.getHead().zRot += smallShake * 0.05F;
        model.getRightArm().xRot = -0.35F + shake * 0.06F;
        model.getLeftArm().xRot = -0.35F - shake * 0.06F;
        model.getRightArm().zRot += 0.32F;
        model.getLeftArm().zRot -= 0.32F;
    }
}
