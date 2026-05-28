package block_party.client.animation.state;

import block_party.client.animation.AbstractAnimation;
import block_party.client.model.IRiggableModel;
import block_party.client.renderers.state.MoeRenderState;
import net.minecraft.util.Mth;

public class BegAnimation extends AbstractAnimation {
    @Override
    public void setRotationAngles(MoeRenderState state, IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float clasp = Mth.sin(ageInTicks * 0.7F) * 0.08F;
        float bounce = Mth.abs(Mth.sin(ageInTicks * 0.32F)) * 0.18F;
        model.getHead().xRot += 0.18F;
        model.getHead().zRot += Mth.sin(ageInTicks * 0.2F) * 0.04F;
        model.getBody().xRot += 0.08F;
        model.getBody().y += bounce;
        model.getRightArm().xRot = -1.15F + clasp;
        model.getLeftArm().xRot = -1.15F - clasp;
        model.getRightArm().yRot = -0.25F;
        model.getLeftArm().yRot = 0.25F;
        model.getRightArm().zRot = 0.12F;
        model.getLeftArm().zRot = -0.12F;
    }
}
