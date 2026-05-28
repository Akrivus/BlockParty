package block_party.client.animation.state;

import block_party.client.animation.AbstractAnimation;
import block_party.client.model.IRiggableModel;
import block_party.client.renderers.state.MoeRenderState;
import net.minecraft.util.Mth;

public class HappyDanceAnimation extends AbstractAnimation {
    @Override
    public void setRotationAngles(MoeRenderState state, IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float bounce = Mth.sin(ageInTicks * 0.55F);
        float sway = Mth.sin(ageInTicks * 0.35F);
        model.getBody().zRot += sway * 0.12F;
        model.getHead().zRot += sway * 0.08F;
        model.getHead().y += Mth.abs(bounce) * -0.35F;
        model.getBody().y += Mth.abs(bounce) * -0.25F;
        model.getRightArm().xRot = -0.55F + bounce * 0.35F;
        model.getLeftArm().xRot = -0.55F - bounce * 0.35F;
        model.getRightArm().zRot += 0.45F + sway * 0.2F;
        model.getLeftArm().zRot -= 0.45F + sway * 0.2F;
        model.getTail().yRot += sway * 0.25F;
        model.getTailTip().yRot += sway * 0.35F;
    }
}
