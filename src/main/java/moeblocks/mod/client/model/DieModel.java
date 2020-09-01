package moeblocks.mod.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moeblocks.mod.entity.DieEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class DieModel<T extends DieEntity> extends EntityModel<T> {
    public ModelRenderer die;

    public DieModel() {
        this.textureHeight = this.textureWidth = 64;
        this.die = new ModelRenderer(this, 0, 0);
        this.die.setRotationPoint(0.0F, 16.0F, 0.0F);
        this.die.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
    }

    @Override
    public void render(MatrixStack stack, IVertexBuilder buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.die.render(stack, buffer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void setRotationAngles(DieEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch) {
        this.die.rotateAngleX = entity.getRotations().getX() * 0.0174533F;
        this.die.rotateAngleY = entity.getRotations().getY() * 0.0174533F;
        this.die.rotateAngleZ = entity.getRotations().getZ() * 0.0174533F;
    }
}