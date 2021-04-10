package moeblocks.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moeblocks.entity.DeerEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class DeerModel<T extends DeerEntity> extends EntityModel<T> {
    private final ModelRenderer body;
    private final ModelRenderer neck;
    private final ModelRenderer neckJoint;
    private final ModelRenderer head;
    private final ModelRenderer rightAntler;
    private final ModelRenderer leftAntler;
    private final ModelRenderer leftEar;
    private final ModelRenderer rightEar;
    private final ModelRenderer snout;
    private final ModelRenderer frontLeftLeg;
    private final ModelRenderer frontRightLeg;
    private final ModelRenderer backLeftLeg;
    private final ModelRenderer backLeftLegTop;
    private final ModelRenderer backLeftLegBottom;
    private final ModelRenderer backRightLeg;
    private final ModelRenderer backRightLegTop;
    private final ModelRenderer backRightLegBottom;
    private final ModelRenderer tail;

    public DeerModel() {
        this.textureWidth = this.textureHeight = 64;
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, 8.0F, -8.0F);
        this.body.addBox(-3.0F, -2.0F, 0.0F, 6, 8, 12, 0.0F);
        this.neck = new ModelRenderer(this, 24, 8);
        this.neck.setRotationPoint(0.0F, 1.3F, 1.0F);
        this.neck.addBox(-2.0F, -2.0F, -9.0F, 4, 4, 12, 0.0F);
        this.neck.rotateAngleX = -1.2747884856566583F;
        this.body.addChild(this.neck);
        this.neckJoint = new ModelRenderer(this, 0, 0);
        this.neckJoint.setRotationPoint(0.0F, 0.0F, -9.0F);
        this.neckJoint.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        this.neckJoint.rotateAngleX = 1.4114477660878142F;
        this.neck.addChild(this.neckJoint);
        this.head = new ModelRenderer(this, 0, 20);
        this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.head.addBox(-2.5F, -2.0F, -3.0F, 5, 4, 5, 0.0F);
        this.head.rotateAngleY = -0.012735471695871659F;
        this.neckJoint.addChild(this.head);
        this.leftAntler = new ModelRenderer(this, 0, 34);
        this.leftAntler.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.leftAntler.addBox(-12.0F, -12.0F, 0.0F, 12, 12, 0, 0.0F);
        this.leftAntler.rotateAngleX = -0.3490658503988659F;
        this.leftAntler.rotateAngleY = 0.5235987755982988F;
        this.leftAntler.rotateAngleZ = -0.3490658503988659F;
        this.head.addChild(this.leftAntler);
        this.rightAntler = new ModelRenderer(this, 0, 34);
        this.rightAntler.mirror = true;
        this.rightAntler.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.rightAntler.addBox(0.0F, -12.0F, 0.0F, 12, 12, 0, 0.0F);
        this.rightAntler.rotateAngleX = -0.3490658503988659F;
        this.rightAntler.rotateAngleY = -0.5235987755982988F;
        this.rightAntler.rotateAngleZ = -0.3490658503988659F;
        this.head.addChild(this.rightAntler);
        this.leftEar = new ModelRenderer(this, 0, 29);
        this.leftEar.mirror = true;
        this.leftEar.setRotationPoint(2.0F, 0.0F, 0.0F);
        this.leftEar.addBox(0.0F, -4.0F, 0.0F, 4, 4, 0, 0.0F);
        this.leftEar.rotateAngleX = -0.17453292519943295F;
        this.leftEar.rotateAngleZ = 0.3490658503988659F;
        this.head.addChild(this.leftEar);
        this.rightEar = new ModelRenderer(this, 0, 29);
        this.rightEar.setRotationPoint(-2.0F, 0.0F, 0.0F);
        this.rightEar.addBox(-4.0F, -4.0F, 0.0F, 4, 4, 0, 0.0F);
        this.rightEar.rotateAngleX = -0.17453292519943295F;
        this.rightEar.rotateAngleZ = -0.3490658503988659F;
        this.head.addChild(this.rightEar);
        this.snout = new ModelRenderer(this, 32, 0);
        this.snout.setRotationPoint(0.0F, 0.0F, -4.0F);
        this.snout.addBox(-1.5F, -1.0F, -2.0F, 3, 3, 3, 0.0F);
        this.head.addChild(this.snout);
        this.frontLeftLeg = new ModelRenderer(this, 0, 0);
        this.frontLeftLeg.setRotationPoint(2.0F, 6.0F, 1.0F);
        this.frontLeftLeg.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2, 0.0F);
        this.body.addChild(this.frontLeftLeg);
        this.frontRightLeg = new ModelRenderer(this, 0, 0);
        this.frontRightLeg.setRotationPoint(-2.0F, 6.0F, 1.0F);
        this.frontRightLeg.addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2, 0.0F);
        this.body.addChild(this.frontRightLeg);
        this.backLeftLeg = new ModelRenderer(this, 0, 0);
        this.backLeftLeg.setRotationPoint(3.0F, 6.0F, 9.2F);
        this.backLeftLeg.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        this.body.addChild(this.backLeftLeg);
        this.backLeftLegTop = new ModelRenderer(this, 44, 9);
        this.backLeftLegTop.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.backLeftLegTop.addBox(-2.0F, -2.0F, -0.7F, 3, 6, 4, 0.0F);
        this.backLeftLegTop.rotateAngleX = 0.7853981633974483F;
        this.backLeftLeg.addChild(this.backLeftLegTop);
        this.backLeftLegBottom = new ModelRenderer(this, 54, 22);
        this.backLeftLegBottom.setRotationPoint(-0.5F, 4.0F, 0.9F);
        this.backLeftLegBottom.addBox(-1.0F, -1.0F, 0.0F, 2, 9, 2, 0.0F);
        this.backLeftLegBottom.rotateAngleX = -0.6829473363053812F;
        this.backLeftLegTop.addChild(this.backLeftLegBottom);
        this.backRightLeg = new ModelRenderer(this, 0, 0);
        this.backRightLeg.setRotationPoint(-3.0F, 6.0F, 9.2F);
        this.backRightLeg.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        this.body.addChild(this.backRightLeg);
        this.backRightLegTop = new ModelRenderer(this, 44, 9);
        this.backRightLegTop.mirror = true;
        this.backRightLegTop.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.backRightLegTop.addBox(-1.0F, -2.0F, -0.7F, 3, 6, 4, 0.0F);
        this.backRightLegTop.rotateAngleX = 0.7853981633974483F;
        this.backRightLeg.addChild(this.backRightLegTop);
        this.backRightLegBottom = new ModelRenderer(this, 54, 22);
        this.backRightLegBottom.mirror = true;
        this.backRightLegBottom.setRotationPoint(0.5F, 4.0F, 0.9F);
        this.backRightLegBottom.addBox(-1.0F, -1.0F, 0.0F, 2, 9, 2, 0.0F);
        this.backRightLegTop.addChild(this.backRightLegBottom);
        this.backRightLegBottom.rotateAngleX = -0.6829473363053812F;
        this.tail = new ModelRenderer(this, 44, 0);
        this.tail.setRotationPoint(0.0F, -2.0F, 12.0F);
        this.tail.addBox(-1.5F, 0.0F, -3.0F, 3, 6, 3, 0.0F);
        this.tail.rotateAngleX = 0.5009094953223726F;
        this.body.addChild(this.tail);
    }

    @Override
    public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack stack, IVertexBuilder buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.body.render(stack, buffer, light, overlay, red, green, blue, alpha);
    }
}