package block_party.client.animation;

import block_party.client.model.IRiggableModel;
import block_party.npc.BlockPartyNPC;
import com.mojang.blaze3d.vertex.PoseStack;

public abstract class AbstractAnimation {
    public abstract void tick(BlockPartyNPC entity);

    public abstract void setRotationAngles(BlockPartyNPC entity, IRiggableModel model, float limbSwing, float limbSwingAmount, float ageInTicks);

    public abstract void render(BlockPartyNPC entity, PoseStack stack, float partialTickTime);
}
