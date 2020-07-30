package mod.moeblocks.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mod.moeblocks.entity.MoeEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;

public class MoeModel<T extends MoeEntity> extends EntityModel<T> implements IHasArm, IHasHead {
    public ModelRenderer head;
    public ModelRenderer hair;
    public ModelRenderer rightBun;
    public ModelRenderer leftBun;
    public ModelRenderer backBun;
    public ModelRenderer rightTail;
    public ModelRenderer leftTail;
    public ModelRenderer backTail;
    public ModelRenderer rightEar;
    public ModelRenderer leftEar;
    public ModelRenderer hatTop;
    public ModelRenderer hatBrim;
    public ModelRenderer body;
    public ModelRenderer skirt;
    public ModelRenderer rightWing;
    public ModelRenderer leftWing;
    public ModelRenderer tailBase;
    public ModelRenderer tailTip;
    public ModelRenderer rightArm;
    public ModelRenderer leftArm;
    public ModelRenderer rightLeg;
    public ModelRenderer leftLeg;
    public ModelRenderer headWear;
    public ModelRenderer hairWear;
    public ModelRenderer rightBunWear;
    public ModelRenderer leftBunWear;
    public ModelRenderer backBunWear;
    public ModelRenderer rightTailWear;
    public ModelRenderer leftTailWear;
    public ModelRenderer backTailWear;
    public ModelRenderer rightEarWear;
    public ModelRenderer leftEarWear;
    public ModelRenderer bodyWear;
    public ModelRenderer skirtWear;
    public ModelRenderer tailBaseWear;
    public ModelRenderer tailTipWear;
    public ModelRenderer rightArmWear;
    public ModelRenderer leftArmWear;
    public ModelRenderer rightLegWear;
    public ModelRenderer leftLegWear;

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
    public void setRotationAngles(MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch) {
        this.head.rotateAngleX = pitch * 0.0174532925F;
        this.head.rotateAngleY = yaw * 0.0174532925F;
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
        this.rightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.14F) * 2.0F * limbSwingAmount * 0.5F;
        this.rightArm.rotateAngleX += MathHelper.sin(entity.ticksExisted * 0.067F) * 0.05F;
        this.rightArm.rotateAngleZ = -this.rightArm.rotateAngleX * 0.5F;
        this.rightArm.rotateAngleZ += MathHelper.cos(entity.ticksExisted * 0.09F) * 0.05F + 0.05F;
        this.leftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.leftArm.rotateAngleZ = -this.leftArm.rotateAngleX * 0.5F;
        this.leftArm.rotateAngleX -= MathHelper.sin(entity.ticksExisted * 0.067F) * 0.05F;
        this.leftArm.rotateAngleZ -= MathHelper.cos(entity.ticksExisted * 0.09F) * 0.05F + 0.05F;
        this.rightArm.rotateAngleZ += 0.7853981633974483F;
        this.leftArm.rotateAngleZ -= 0.7853981633974483F;
        this.rightWing.rotateAngleX = (this.leftWing.rotateAngleX = -0.23561947F);
        this.rightWing.rotateAngleY = -(this.leftWing.rotateAngleY = entity.onGround ? 0.0F : 0.47123894F + MathHelper.cos(entity.ticksExisted * partialTicks) * 3.14F * 0.05F);
        this.rightWing.rotateAngleZ = -(this.leftWing.rotateAngleZ = -0.23561947F);
        this.rightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 3.14F) * 1.4F * limbSwingAmount;
        this.skirt.rotateAngleY = -this.leftLeg.rotateAngleX * 0.25F;
        this.tailBase.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.tailBase.rotateAngleZ = -this.tailBase.rotateAngleX * 0.5F - MathHelper.cos(entity.ticksExisted * 0.09F) * 0.05F + 0.05F;
        entity.getAnimation().render(entity, limbSwing, limbSwingAmount, partialTicks);
    }

    @Override
    public void translateHand(HandSide side, MatrixStack stack) {
        float x = side == HandSide.RIGHT ? 1.0F : -1.0F;
        float y = -0.125F;
        float z = 0.0F;
        ModelRenderer arm = this.getArmForSide(side);
        arm.rotationPointX += x;
        arm.translateRotate(stack);
        stack.translate(z, y, z);
        arm.rotationPointX -= x;
    }

    protected ModelRenderer getArmForSide(HandSide side) {
        return side == HandSide.LEFT ? this.leftArm : this.rightArm;
    }

    @Override
    public ModelRenderer getModelHead() {
        return this.head;
    }

    protected HandSide getMainHand(MoeEntity entity) {
        HandSide side = entity.getPrimaryHand();
        if (entity.swingingHand != Hand.MAIN_HAND) {
            return side.opposite();
        }
        return side;
    }
}