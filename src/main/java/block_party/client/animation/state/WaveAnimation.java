package block_party.client.animation.state;

import block_party.client.animation.AbstractAnimation;
import block_party.client.model.IRiggableModel;
import block_party.npc.BlockPartyNPC;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.HumanoidArm;

public class WaveAnimation extends AbstractAnimation {
    @Override
    public void tick(BlockPartyNPC entity) {

    }

    @Override
    public void setRotationAngles(BlockPartyNPC entity, IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks) {
        float wave = (float) (Math.sin(ageInTicks / 3) / 2 - Math.toRadians(120));
        model.getArmForSide(HumanoidArm.LEFT).setRotation(0, 0, wave);
        model.getHead().zRot = (float) (Math.toRadians(345));
        model.getBody().zRot = (float) (Math.toRadians(350));
    }

    @Override
    public void render(BlockPartyNPC entity, PoseStack stack, float partialTickTime) {

    }
}
