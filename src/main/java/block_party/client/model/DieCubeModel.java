package block_party.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class DieCubeModel<T extends AbstractDie> extends EntityModel<T> {
    private final ModelPart root;
    private final ModelPart die;

    public DieCubeModel(ModelPart root) {
        this.root = root;
        this.die = this.root.getChild("die");
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.die.render(stack, buffer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(AbstractDie entity, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch) {
        this.die.xRot = entity.getRotations().getX() * 0.0174532925F;
        this.die.yRot = entity.getRotations().getY() * 0.0174532925F;
        this.die.zRot = entity.getRotations().getZ() * 0.0174532925F;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition parts = new MeshDefinition();
        PartDefinition root = parts.getRoot();
        root.addOrReplaceChild("die", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(parts, 64, 64);
    }
}