package block_party.client.animation;

import block_party.client.model.IRiggableModel;
import block_party.client.renderers.state.MoeRenderState;
import com.mojang.blaze3d.vertex.PoseStack;

public abstract class AbstractAnimation {
    public void tick(MoeRenderState state) {
    }

    public abstract void setRotationAngles(MoeRenderState state, IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks);

    public void render(MoeRenderState state, PoseStack stack, float partialTickTime) {
    }
}
