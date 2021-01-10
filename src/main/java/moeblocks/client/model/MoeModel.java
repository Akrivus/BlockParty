package moeblocks.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import moeblocks.entity.MoeEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

public class MoeModel<T extends MoeEntity> extends EntityModel<T> implements IHasArm, IHasHead, IRiggableModel {
    private final ModelRenderer head;
    private final ModelRenderer hair;
    private final ModelRenderer rightBun;
    private final ModelRenderer leftBun;
    private final ModelRenderer backBun;
    private final ModelRenderer rightTail;
    private final ModelRenderer leftTail;
    private final ModelRenderer backTail;
    private final ModelRenderer rightEar;
    private final ModelRenderer leftEar;
    private final ModelRenderer hatTop;
    private final ModelRenderer hatBrim;
    private final ModelRenderer body;
    private final ModelRenderer tiddies;
    private final ModelRenderer skirt;
    private final ModelRenderer rightWing;
    private final ModelRenderer leftWing;
    private final ModelRenderer tailBase;
    private final ModelRenderer tailTip;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightLeg;
    private final ModelRenderer leftLeg;
    private final ModelRenderer headWear;
    private final ModelRenderer hairWear;
    private final ModelRenderer rightBunWear;
    private final ModelRenderer leftBunWear;
    private final ModelRenderer backBunWear;
    private final ModelRenderer rightTailWear;
    private final ModelRenderer leftTailWear;
    private final ModelRenderer backTailWear;
    private final ModelRenderer rightEarWear;
    private final ModelRenderer leftEarWear;
    private final ModelRenderer bodyWear;
    private final ModelRenderer tiddiesWear;
    private final ModelRenderer skirtWear;
    private final ModelRenderer tailBaseWear;
    private final ModelRenderer tailTipWear;
    private final ModelRenderer rightArmWear;
    private final ModelRenderer leftArmWear;
    private final ModelRenderer rightLegWear;
    private final ModelRenderer leftLegWear;
    
    public MoeModel() {
        this.textureHeight = this.textureWidth = 128;
        this.head = new ModelRenderer(this, 0, 0);
        this.head.setRotationPoint(0.0F, 12.0F, 0.0F);
        this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F);
        this.hair = new ModelRenderer(this, 32, 0);
        this.hair.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hair.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 20.0F, 8.0F, 0.5F);
        this.head.addChild(this.hair);
        this.rightBun = new ModelRenderer(this, 64, 9);
        this.rightBun.setRotationPoint(-4.0F, -5.0F, 0.0F);
        this.rightBun.addBox(-3.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F);
        this.head.addChild(this.rightBun);
        this.rightTail = new ModelRenderer(this, 64, 9);
        this.rightTail.setRotationPoint(4.0F, -5.0F, 0.0F);
        this.rightTail.addBox(0.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, 0.01F);
        this.head.addChild(this.rightTail);
        this.leftBun = new ModelRenderer(this, 64, 0);
        this.leftBun.setRotationPoint(4.0F, -5.0F, 0.0F);
        this.leftBun.addBox(0.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F);
        this.head.addChild(this.leftBun);
        this.leftTail = new ModelRenderer(this, 64, 0);
        this.leftTail.setRotationPoint(-4.0F, -5.0F, 0.0F);
        this.leftTail.addBox(-3.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, 0.01F);
        this.head.addChild(this.leftTail);
        this.backBun = new ModelRenderer(this, 64, 18);
        this.backBun.setRotationPoint(0.0F, -5.0F, 4.0F);
        this.backBun.addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F);
        this.head.addChild(this.backBun);
        this.backTail = new ModelRenderer(this, 64, 18);
        this.backTail.setRotationPoint(0.0F, -5.0F, 4.0F);
        this.backTail.addBox(-1.5F, -1.5F, 0.0F, 3.0F, 6.0F, 3.0F);
        this.head.addChild(this.backTail);
        this.rightEar = new ModelRenderer(this, 16, 22);
        this.rightEar.setRotationPoint(-3.0F, -8.0F, -0.5F);
        this.rightEar.addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F);
        this.head.addChild(this.rightEar);
        this.leftEar = new ModelRenderer(this, 8, 22);
        this.leftEar.setRotationPoint(3.0F, -8.0F, -0.5F);
        this.leftEar.addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F);
        this.head.addChild(this.leftEar);
        this.hatTop = new ModelRenderer(this, 0, 40);
        this.hatTop.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.hatTop.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 8.0F, 0.01F);
        this.head.addChild(this.hatTop);
        this.hatBrim = new ModelRenderer(this, -16, 52);
        this.hatBrim.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hatBrim.addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F);
        this.hatTop.addChild(this.hatBrim);
        this.body = new ModelRenderer(this, 6, 28);
        this.body.setRotationPoint(0.0F, 15.0F, 0.0F);
        this.body.addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F);
        this.tiddies = new ModelRenderer(this, 6, 28);
        this.tiddies.setRotationPoint(0.0F, -2.9F, -2.0F);
        this.tiddies.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 4.0F, -0.01F);
        this.body.addChild(this.tiddies);
        this.skirt = new ModelRenderer(this, 24, 28);
        this.skirt.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.skirt.addBox(-6.0F, 0.0F, -6.0F, 12.0F, 8.0F, 12.0F);
        this.body.addChild(this.skirt);
        this.rightWing = new ModelRenderer(this, 104, 0);
        this.rightWing.setRotationPoint(1.0F, 0.0F, 2.0F);
        this.rightWing.addBox(0.0F, -12.0F, 0.0F, 12.0F, 12.0F, 0.0F);
        this.body.addChild(this.rightWing);
        this.leftWing = new ModelRenderer(this, 80, 0);
        this.leftWing.setRotationPoint(-1.0F, 0.0F, 2.0F);
        this.leftWing.addBox(-12.0F, -12.0F, 0.0F, 12.0F, 12.0F, 0.0F);
        this.body.addChild(this.leftWing);
        this.tailBase = new ModelRenderer(this, 11, 17);
        this.tailBase.setRotationPoint(0.0F, 2.5F, 2.0F);
        this.tailBase.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F);
        this.tailBase.rotateAngleX = 0.10471975511965977F;
        this.body.addChild(this.tailBase);
        this.tailTip = new ModelRenderer(this, 10, 16);
        this.tailTip.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.tailTip.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 5.0F);
        this.tailTip.rotateAngleX = 0.15707963267948966F;
        this.tailBase.addChild(this.tailTip);
        this.rightArm = new ModelRenderer(this, 0, 24);
        this.rightArm.setRotationPoint(-4.0F, 12.0F, 0.0F);
        this.rightArm.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F);
        this.leftArm = new ModelRenderer(this, 0, 16);
        this.leftArm.setRotationPoint(4.0F, 12.0F, 0.0F);
        this.leftArm.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F);
        this.rightLeg = new ModelRenderer(this, 24, 24);
        this.rightLeg.setRotationPoint(-2.0F, 18.0F, 0.0F);
        this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F);
        this.leftLeg = new ModelRenderer(this, 24, 16);
        this.leftLeg.setRotationPoint(2.0F, 18.0F, 0.0F);
        this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F);
        this.headWear = new ModelRenderer(this, 0, 0 + 68);
        this.headWear.setRotationPoint(0.0F, 12.0F, 0.0F);
        this.headWear.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.5F);
        this.hairWear = new ModelRenderer(this, 32, 0 + 68);
        this.hairWear.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.hairWear.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 20.0F, 8.0F, 0.5F + 0.5F);
        this.headWear.addChild(this.hairWear);
        this.rightBunWear = new ModelRenderer(this, 64, 9 + 68);
        this.rightBunWear.setRotationPoint(-4.0F, -5.0F, 0.0F);
        this.rightBunWear.addBox(-3.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, 0.5F);
        this.headWear.addChild(this.rightBunWear);
        this.rightTailWear = new ModelRenderer(this, 64, 9 + 68);
        this.rightTailWear.setRotationPoint(4.0F, -5.0F, 0.0F);
        this.rightTailWear.addBox(0.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, 0.5F + 0.01F);
        this.headWear.addChild(this.rightTailWear);
        this.leftBunWear = new ModelRenderer(this, 64, 0 + 68);
        this.leftBunWear.setRotationPoint(4.0F, -5.0F, 0.0F);
        this.leftBunWear.addBox(0.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, 0.5F);
        this.headWear.addChild(this.leftBunWear);
        this.leftTailWear = new ModelRenderer(this, 64, 0 + 68);
        this.leftTailWear.setRotationPoint(-4.0F, -5.0F, 0.0F);
        this.leftTailWear.addBox(-3.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, 0.5F + 0.01F);
        this.headWear.addChild(this.leftTailWear);
        this.backBunWear = new ModelRenderer(this, 64, 18 + 68);
        this.backBunWear.setRotationPoint(0.0F, -5.0F, 4.0F);
        this.backBunWear.addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F, 0.5F);
        this.headWear.addChild(this.backBunWear);
        this.backTailWear = new ModelRenderer(this, 64, 18 + 68);
        this.backTailWear.setRotationPoint(0.0F, -5.0F, 4.0F);
        this.backTailWear.addBox(-1.5F, -1.5F, 0.0F, 3.0F, 6.0F, 3.0F, 0.5F);
        this.headWear.addChild(this.backTailWear);
        this.rightEarWear = new ModelRenderer(this, 16, 22 + 68);
        this.rightEarWear.setRotationPoint(-3.0F, -8.0F, -0.5F);
        this.rightEarWear.addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.5F);
        this.headWear.addChild(this.rightEarWear);
        this.leftEarWear = new ModelRenderer(this, 8, 22 + 68);
        this.leftEarWear.setRotationPoint(3.0F, -8.0F, -0.5F);
        this.leftEarWear.addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.5F);
        this.headWear.addChild(this.leftEarWear);
        this.bodyWear = new ModelRenderer(this, 6, 28 + 68);
        this.bodyWear.setRotationPoint(0.0F, 15.0F, 0.0F);
        this.bodyWear.addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, 0.5F);
        this.tiddiesWear = new ModelRenderer(this, 6, 28 + 68);
        this.tiddiesWear.setRotationPoint(0.0F, -2.9F, -2.0F);
        this.tiddiesWear.addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 4.0F, 0.49F);
        this.bodyWear.addChild(this.tiddiesWear);
        this.skirtWear = new ModelRenderer(this, 24, 28 + 68);
        this.skirtWear.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.skirtWear.addBox(-6.0F, 0.0F, -6.0F, 12.0F, 8.0F, 12.0F, 0.5F);
        this.bodyWear.addChild(this.skirtWear);
        this.tailBaseWear = new ModelRenderer(this, 11, 17 + 68);
        this.tailBaseWear.setRotationPoint(0.0F, 2.5F, 2.0F);
        this.tailBaseWear.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F, 0.5F);
        this.tailBaseWear.rotateAngleX = 0.10471975511965977F;
        this.bodyWear.addChild(this.tailBaseWear);
        this.tailTipWear = new ModelRenderer(this, 10, 16 + 68);
        this.tailTipWear.setRotationPoint(0.0F, 0.0F, 4.0F);
        this.tailTipWear.addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 5.0F, 0.5F);
        this.tailBaseWear.addChild(this.tailTipWear);
        this.rightArmWear = new ModelRenderer(this, 0, 24 + 68);
        this.rightArmWear.setRotationPoint(-4.0F, 12.0F, 0.0F);
        this.rightArmWear.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.5F);
        this.leftArmWear = new ModelRenderer(this, 0, 16 + 68);
        this.leftArmWear.setRotationPoint(4.0F, 12.0F, 0.0F);
        this.leftArmWear.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.5F);
        this.rightLegWear = new ModelRenderer(this, 24, 24 + 68);
        this.rightLegWear.setRotationPoint(-2.0F, 18.0F, 0.0F);
        this.rightLegWear.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.5F);
        this.leftLegWear = new ModelRenderer(this, 24, 16 + 68);
        this.leftLegWear.setRotationPoint(2.0F, 18.0F, 0.0F);
        this.leftLegWear.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, 0.5F);
    }
    
    @Override
    public ModelRenderer getModelHead() {
        return this.head;
    }
    
    @Override
    public void render(MatrixStack stack, IVertexBuilder buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.body.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.bodyWear.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.rightArm.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.rightArmWear.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.leftArm.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.leftArmWear.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.rightLeg.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.rightLegWear.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.leftLeg.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.leftLegWear.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.head.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.headWear.render(stack, buffer, light, overlay, red, green, blue, alpha);
    }
    
    @Override
    public void setRotationAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch) {
        entity.states.forEach((state, machine) -> machine.setRotationAngles(this, limbSwing, limbSwingAmount, ageInTicks));
        this.rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        this.rightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.leftArm.rotateAngleX += -MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        this.leftArm.rotateAngleZ += -MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.head.rotateAngleX += pitch * 0.0174532925F;
        this.head.rotateAngleY += yaw * 0.0174532925F;
        this.headWear.copyModelAngles(this.head);
        this.hairWear.copyModelAngles(this.hair);
        this.rightBunWear.copyModelAngles(this.rightBun);
        this.leftBunWear.copyModelAngles(this.leftBun);
        this.backBunWear.copyModelAngles(this.backBun);
        this.rightTailWear.copyModelAngles(this.rightTail);
        this.leftTailWear.copyModelAngles(this.leftTail);
        this.backTailWear.copyModelAngles(this.backTail);
        this.rightEarWear.copyModelAngles(this.rightEar);
        this.leftEarWear.copyModelAngles(this.leftEar);
        this.bodyWear.copyModelAngles(this.body);
        this.tiddiesWear.copyModelAngles(this.tiddies);
        this.skirtWear.copyModelAngles(this.skirt);
        this.tailBaseWear.copyModelAngles(this.tailBase);
        this.tailTipWear.copyModelAngles(this.tailTip);
        this.rightArmWear.copyModelAngles(this.rightArm);
        this.leftArmWear.copyModelAngles(this.leftArm);
        this.rightLegWear.copyModelAngles(this.rightLeg);
        this.leftLegWear.copyModelAngles(this.leftLeg);
    }
    
    @Override
    public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
        this.rightArm.rotationPointX = -(this.leftArm.rotationPointX = 4.0F + (this.leftArm.rotationPointZ = this.rightArm.rotationPointZ = 0.0F));
        this.head.rotateAngleX = this.head.rotateAngleY = this.head.rotateAngleZ = this.body.rotateAngleX = this.body.rotateAngleY = this.body.rotateAngleZ = 0.0F;
        this.rightArm.rotateAngleY = this.leftArm.rotateAngleY = 0.0F;
        this.rightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.14F) * 2.0F * limbSwingAmount * 0.5F;
        this.rightArm.rotateAngleZ = 0.7853981633974483F;
        this.leftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.leftArm.rotateAngleZ = -0.7853981633974483F;
        this.rightWing.rotateAngleX = (this.leftWing.rotateAngleX = -0.23561947F);
        this.rightWing.rotateAngleY = -(this.leftWing.rotateAngleY = entity.isOnGround() ? 0.0F : 0.47123894F + MathHelper.cos(entity.ticksExisted * partialTicks) * 3.14F * 0.05F);
        this.rightWing.rotateAngleZ = -(this.leftWing.rotateAngleZ = -0.23561947F);
        this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.rightLeg.rotateAngleY = this.rightLeg.rotateAngleZ = 0.0F;
        this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.14F) * 1.4F * limbSwingAmount;
        this.leftLeg.rotateAngleY = this.leftLeg.rotateAngleZ = 0.0F;
        this.tiddies.rotateAngleX = entity.getCupSize().ordinal() * -0.218166156F;
        this.skirt.rotateAngleY = -this.leftLeg.rotateAngleX * 0.25F;
        this.tailBase.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.tailBase.rotateAngleZ = -this.tailBase.rotateAngleX * 0.5F - MathHelper.cos(entity.ticksExisted * 0.09F) * 0.05F + 0.05F;
        
        if (entity.isSwingInProgress) { this.setSwingingArmRotations(entity, partialTicks); }
        if (entity.isSneaking()) {
            this.head.rotationPointY = 14.2F;
            this.body.rotationPointY = 16.2F;
            this.body.rotateAngleX = 0.5F;
            this.rightArm.rotateAngleX += 0.4F;
            this.rightArm.rotationPointY = 15.2F;
            this.leftArm.rotateAngleX += 0.4F;
            this.leftArm.rotationPointY = 15.2F;
            this.rightLeg.rotationPointY = 19.2F;
            this.rightLeg.rotationPointZ = 1.0F;
            this.leftLeg.rotationPointY = 19.2F;
            this.leftLeg.rotationPointZ = 1.0F;
        } else {
            this.head.rotationPointY = 12.0F;
            this.body.rotationPointY = 15.0F;
            this.body.rotateAngleX = 0.0F;
            this.rightArm.rotationPointY = 12.0F;
            this.leftArm.rotationPointY = 12.0F;
            this.rightLeg.rotationPointY = 18.0F;
            this.rightLeg.rotationPointZ = 0.0F;
            this.leftLeg.rotationPointY = 18.0F;
            this.leftLeg.rotationPointZ = 0.0F;
        }
        if (entity.isSitting()) {
            this.rightArm.rotateAngleX -= 0.6283192F;
            this.leftArm.rotateAngleX -= 0.6283192F;
            this.rightLeg.rotateAngleX = -1.4137167F;
            this.rightLeg.rotateAngleY = 0.3141592F;
            this.rightLeg.rotateAngleZ = 0.0785398F;
            this.leftLeg.rotateAngleX = -1.4137167F;
            this.leftLeg.rotateAngleY = -0.3141592F;
            this.leftLeg.rotateAngleZ = -0.0785398F;
        }
    }
    
    protected void setSwingingArmRotations(T entity, float partialTicks) {
        float swingProgress = entity.getSwingProgress(partialTicks) * 3.14F;
        HandSide hand = this.getSwingingHand(entity);
        this.body.rotateAngleY = MathHelper.sin(MathHelper.sqrt(swingProgress) * 6.28F) * 0.2F;
        if (hand == HandSide.LEFT) { this.body.rotateAngleY *= -1.0F; }
        this.rightArm.rotationPointX = -(this.leftArm.rotationPointX = MathHelper.cos(this.body.rotateAngleY) * 5.0F);
        this.leftArm.rotationPointZ = -(this.rightArm.rotationPointZ = MathHelper.sin(this.body.rotateAngleY) * 5.0F);
        this.leftArm.rotateAngleX += this.body.rotateAngleY;
        this.rightArm.rotateAngleY += this.body.rotateAngleY;
        this.leftArm.rotateAngleY += this.body.rotateAngleY;
        swingProgress = (float) (1.0F - Math.pow(Math.pow(1.0F - swingProgress, 2), 2));
        float swingRotation = MathHelper.sin(swingProgress * 3.14F);
        float swingHeadings = swingRotation * -(this.head.rotateAngleX - 0.7F) * 0.5F;
        ModelRenderer arm = this.getArmForSide(hand);
        arm.rotateAngleX = arm.rotateAngleX - (swingRotation * 1.2F + swingHeadings);
        arm.rotateAngleY += this.body.rotateAngleY * 2.0F;
        arm.rotateAngleZ += swingRotation * -0.4F;
    }
    
    protected HandSide getSwingingHand(T entity) {
        HandSide hand = entity.getPrimaryHand();
        return entity.swingingHand == Hand.MAIN_HAND ? hand : hand.opposite();
    }
    
    @Override
    public void translateHand(HandSide side, MatrixStack stack) {
        float x = side == HandSide.RIGHT ? 1.0F : -1.0F;
        float y = -0.15F;
        float z = 0.025F;
        ModelRenderer arm = this.getArmForSide(side);
        arm.rotationPointX += x;
        arm.translateRotate(stack);
        stack.translate(z, y, z);
        arm.rotationPointX -= x;
    }
    
    @Override
    public ModelRenderer getRightArm() {
        return this.rightArm;
    }
    
    @Override
    public ModelRenderer getLeftArm() {
        return this.leftArm;
    }
    
    @Override
    public ModelRenderer getRightWing() {
        return this.rightWing;
    }
    
    @Override
    public ModelRenderer getLeftWing() {
        return this.leftWing;
    }
    
    @Override
    public ModelRenderer getRightLeg() {
        return this.rightLeg;
    }
    
    @Override
    public ModelRenderer getLeftLeg() {
        return this.leftLeg;
    }
    
    @Override
    public ModelRenderer getHead() {
        return this.head;
    }
    
    @Override
    public ModelRenderer getHair() {
        return this.headWear;
    }
    
    @Override
    public ModelRenderer getBody() {
        return this.body;
    }
    
    @Override
    public ModelRenderer getTail() {
        return this.tailBase;
    }
    
    @Override
    public ModelRenderer getTailTip() {
        return this.tailTip;
    }
}