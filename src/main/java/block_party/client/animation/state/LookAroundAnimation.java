package block_party.client.animation.state;

import block_party.client.animation.AbstractAnimation;
import block_party.client.model.IRiggableModel;
import block_party.client.renderers.state.MoeRenderState;
import net.minecraft.util.Mth;

public class LookAroundAnimation extends AbstractAnimation {
    @Override
    public void setRotationAngles(MoeRenderState state, IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float glance = Mth.sin(ageInTicks * 0.18F);
        float tilt = Mth.sin(ageInTicks * 0.11F);
        model.getHead().yRot += glance * 0.75F;
        model.getHead().zRot += tilt * 0.08F;
        model.getBody().yRot += glance * 0.1F;
        model.getRightArm().zRot += 0.12F;
        model.getLeftArm().zRot -= 0.12F;
    }
}
