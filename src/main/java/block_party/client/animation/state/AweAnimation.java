package block_party.client.animation.state;

import block_party.client.animation.AbstractAnimation;
import block_party.client.model.IRiggableModel;
import block_party.client.renderers.state.MoeRenderState;
import net.minecraft.util.Mth;

public class AweAnimation extends AbstractAnimation {
    @Override
    public void setRotationAngles(MoeRenderState state, IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float breath = Mth.sin(ageInTicks * 0.18F);
        model.getHead().xRot -= 0.32F + breath * 0.03F;
        model.getHead().zRot += Mth.sin(ageInTicks * 0.11F) * 0.05F;
        model.getBody().xRot -= 0.08F;
        model.getRightArm().xRot = -0.8F + breath * 0.08F;
        model.getLeftArm().xRot = -0.8F - breath * 0.08F;
        model.getRightArm().zRot += 0.22F;
        model.getLeftArm().zRot -= 0.22F;
    }
}
