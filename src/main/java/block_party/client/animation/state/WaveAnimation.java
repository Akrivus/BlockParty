package block_party.client.animation.state;

import block_party.client.animation.AbstractAnimation;
import block_party.client.model.IRiggableModel;
import block_party.client.renderers.state.MoeRenderState;
import net.minecraft.world.entity.HumanoidArm;

public class WaveAnimation extends AbstractAnimation {
    @Override
    public void setRotationAngles(MoeRenderState state, IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float wave = (float) (Math.sin(ageInTicks / 3.0F) / 2.0F - Math.toRadians(120.0D));
        model.getArmForSide(HumanoidArm.LEFT).setRotation(0.0F, 0.0F, wave);
        model.getHead().zRot = (float) Math.toRadians(345.0D);
        model.getBody().zRot = (float) Math.toRadians(350.0D);
    }
}
