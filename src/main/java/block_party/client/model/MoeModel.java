package block_party.client.model;

import block_party.entities.BlockPartyNPC;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;

public class MoeModel<T extends BlockPartyNPC> extends HierarchicalModel<T> implements ArmedModel, HeadedModel, IRiggableModel {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart hair;
    private final ModelPart rightBun;
    private final ModelPart leftBun;
    private final ModelPart backBun;
    private final ModelPart rightTail;
    private final ModelPart leftTail;
    private final ModelPart backTail;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart hatTop;
    private final ModelPart hatBrim;
    private final ModelPart body;
    private final ModelPart bust;
    private final ModelPart skirt;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart tailBase;
    private final ModelPart tailTip;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart headOverlay;
    private final ModelPart hairOverlay;
    private final ModelPart rightBunOverlay;
    private final ModelPart leftBunOverlay;
    private final ModelPart backBunOverlay;
    private final ModelPart rightTailOverlay;
    private final ModelPart leftTailOverlay;
    private final ModelPart backTailOverlay;
    private final ModelPart rightEarOverlay;
    private final ModelPart leftEarOverlay;
    private final ModelPart bodyOverlay;
    private final ModelPart bustOverlay;
    private final ModelPart skirtOverlay;
    private final ModelPart tailBaseOverlay;
    private final ModelPart tailTipOverlay;
    private final ModelPart rightArmOverlay;
    private final ModelPart leftArmOverlay;
    private final ModelPart rightLegOverlay;
    private final ModelPart leftLegOverlay;

    public MoeModel(ModelPart root) {
        this.root = root;
        this.head = this.root.getChild("head");
        this.hair = this.head.getChild("hair");
        this.rightBun = this.head.getChild("right_bun");
        this.rightTail = this.head.getChild("right_tail");
        this.leftBun = this.head.getChild("left_bun");
        this.leftTail = this.head.getChild("left_tail");
        this.backBun = this.head.getChild("back_bun");
        this.backTail = this.head.getChild("back_tail");
        this.rightEar = this.head.getChild("right_ear");
        this.leftEar = this.head.getChild("left_ear");
        this.hatTop = this.head.getChild("hat_top");
        this.hatBrim = this.hatTop.getChild("hat_brim");
        this.body = this.root.getChild("body");
        this.bust = this.body.getChild("bust");
        this.skirt = this.body.getChild("skirt");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
        this.tailBase = this.body.getChild("tail_base");
        this.tailTip = this.tailBase.getChild("tail_tip");
        this.rightArm = this.root.getChild("right_arm");
        this.leftArm = this.root.getChild("left_arm");
        this.rightLeg = this.root.getChild("right_leg");
        this.leftLeg = this.root.getChild("left_leg");
        this.headOverlay = this.root.getChild("head_overlay");
        this.hairOverlay = this.headOverlay.getChild("hair_overlay");
        this.rightBunOverlay = this.headOverlay.getChild("right_bun_overlay");
        this.rightTailOverlay = this.headOverlay.getChild("right_tail_overlay");
        this.leftBunOverlay = this.headOverlay.getChild("left_bun_overlay");
        this.leftTailOverlay = this.headOverlay.getChild("left_tail_overlay");
        this.backBunOverlay = this.headOverlay.getChild("back_bun_overlay");
        this.backTailOverlay = this.headOverlay.getChild("back_tail_overlay");
        this.rightEarOverlay = this.headOverlay.getChild("right_ear_overlay");
        this.leftEarOverlay = this.headOverlay.getChild("left_ear_overlay");
        this.bodyOverlay = this.root.getChild("body_overlay");
        this.bustOverlay = this.bodyOverlay.getChild("bust_overlay");
        this.skirtOverlay = this.bodyOverlay.getChild("skirt_overlay");
        this.tailBaseOverlay = this.bodyOverlay.getChild("tail_base_overlay");
        this.tailTipOverlay = this.tailBaseOverlay.getChild("tail_tip_overlay");
        this.rightArmOverlay = this.root.getChild("right_arm_overlay");
        this.leftArmOverlay = this.root.getChild("left_arm_overlay");
        this.rightLegOverlay = this.root.getChild("right_leg_overlay");
        this.leftLegOverlay = this.root.getChild("left_leg_overlay");
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.body.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.bodyOverlay.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.rightArm.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.rightArmOverlay.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.leftArm.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.leftArmOverlay.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.rightLeg.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.rightLegOverlay.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.leftLeg.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.leftLegOverlay.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.head.render(stack, buffer, light, overlay, red, green, blue, alpha);
        this.headOverlay.render(stack, buffer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch) {
        entity.getAnimation().setRotationAngles(entity, this, limbSwing, limbSwingAmount, ageInTicks);
        this.rightArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
        this.rightArm.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.leftArm.xRot += -Mth.sin(ageInTicks * 0.067F) * 0.05F;
        this.leftArm.zRot += -Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.head.xRot += pitch * 0.0174532925F;
        this.head.yRot += yaw * 0.0174532925F;
        this.headOverlay.copyFrom(this.head);
        this.hairOverlay.copyFrom(this.hair);
        this.rightBunOverlay.copyFrom(this.rightBun);
        this.leftBunOverlay.copyFrom(this.leftBun);
        this.backBunOverlay.copyFrom(this.backBun);
        this.rightTailOverlay.copyFrom(this.rightTail);
        this.leftTailOverlay.copyFrom(this.leftTail);
        this.backTailOverlay.copyFrom(this.backTail);
        this.rightEarOverlay.copyFrom(this.rightEar);
        this.leftEarOverlay.copyFrom(this.leftEar);
        this.bodyOverlay.copyFrom(this.body);
        this.bustOverlay.copyFrom(this.bust);
        this.skirtOverlay.copyFrom(this.skirt);
        this.tailBaseOverlay.copyFrom(this.tailBase);
        this.tailTipOverlay.copyFrom(this.tailTip);
        this.rightArmOverlay.copyFrom(this.rightArm);
        this.leftArmOverlay.copyFrom(this.leftArm);
        this.rightLegOverlay.copyFrom(this.rightLeg);
        this.leftLegOverlay.copyFrom(this.leftLeg);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.rightArm.x = -(this.leftArm.x = 4.0F + (this.leftArm.z = this.rightArm.z = 0.0F));
        this.head.xRot = this.head.yRot = this.head.zRot = this.body.xRot = this.body.yRot = this.body.zRot = 0.0F;
        this.rightArm.yRot = this.leftArm.yRot = 0.0F;
        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + 3.14F) * 2.0F * limbSwingAmount * 0.5F;
        this.rightArm.zRot = 0.7853981633974483F;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.leftArm.zRot = -0.7853981633974483F;
        this.rightWing.xRot = (this.leftWing.xRot = -0.23561947F);
        this.rightWing.yRot = -(this.leftWing.yRot = entity.onGround() ? 0.0F : 0.47123894F + Mth.cos(entity.tickCount * partialTicks) * 3.14F * 0.05F);
        this.rightWing.zRot = -(this.leftWing.zRot = -0.23561947F);
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.rightLeg.yRot = this.rightLeg.zRot = 0.0F;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + 3.14F) * 1.4F * limbSwingAmount;
        this.leftLeg.yRot = this.leftLeg.zRot = 0.0F;
        this.bust.xRot = entity.getSlouch() * -0.024240684F;
        this.skirt.yRot = -this.leftLeg.xRot * 0.25F;
        this.tailBase.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        this.tailBase.zRot = -this.tailBase.xRot * 0.5F - Mth.cos(entity.tickCount * 0.09F) * 0.05F + 0.05F;

        if (entity.swinging) { this.setSwingingArmRotations(entity, partialTicks); }
        if (entity.isShiftKeyDown()) {
            this.head.y = 14.2F;
            this.body.y = 16.2F;
            this.body.xRot = 0.5F;
            this.rightArm.xRot += 0.4F;
            this.rightArm.y = 15.2F;
            this.leftArm.xRot += 0.4F;
            this.leftArm.y = 15.2F;
            this.rightLeg.y = 19.2F;
            this.rightLeg.z = 1.0F;
            this.leftLeg.y = 19.2F;
            this.leftLeg.z = 1.0F;
        } else {
            this.head.y = 12.0F;
            this.body.y = 15.0F;
            this.body.xRot = 0.0F;
            this.rightArm.y = 12.0F;
            this.leftArm.y = 12.0F;
            this.rightLeg.y = 18.0F;
            this.rightLeg.z = 0.0F;
            this.leftLeg.y = 18.0F;
            this.leftLeg.z = 0.0F;
        }
        if (entity.isSitting()) {
            this.rightArm.xRot -= 0.6283192F;
            this.leftArm.xRot -= 0.6283192F;
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = 0.3141592F;
            this.rightLeg.zRot = 0.0785398F;
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = -0.3141592F;
            this.leftLeg.zRot = -0.0785398F;
        }
    }

    protected void setSwingingArmRotations(T entity, float partialTicks) {
        float swingProgress = entity.getAttackAnim(partialTicks) * 3.14F;
        HumanoidArm hand = this.getSwingingHand(entity);
        this.body.yRot = Mth.sin(Mth.sqrt(swingProgress) * 6.28F) * 0.2F;
        if (hand == HumanoidArm.LEFT) { this.body.yRot *= -1.0F; }
        this.rightArm.x = -(this.leftArm.x = Mth.cos(this.body.yRot) * 5.0F);
        this.leftArm.z = -(this.rightArm.z = Mth.sin(this.body.yRot) * 5.0F);
        this.leftArm.xRot += this.body.yRot;
        this.rightArm.yRot += this.body.yRot;
        this.leftArm.yRot += this.body.yRot;
        swingProgress = (float) (1.0F - Math.pow(Math.pow(1.0F - swingProgress, 2), 2));
        float swingRotation = Mth.sin(swingProgress * 3.14F);
        float swingHeadings = swingRotation * -(this.head.xRot - 0.7F) * 0.5F;
        ModelPart arm = this.getArmForSide(hand);
        arm.xRot = arm.xRot - (swingRotation * 1.2F + swingHeadings);
        arm.yRot += this.body.yRot * 2.0F;
        arm.zRot += swingRotation * -0.4F;
    }

    protected HumanoidArm getSwingingHand(T entity) {
        HumanoidArm hand = entity.getMainArm();
        return entity.swingingArm == InteractionHand.MAIN_HAND ? hand : hand.getOpposite();
    }

    @Override
    public void translateToHand(HumanoidArm side, PoseStack stack) {
        float x = side == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float y = -0.15F;
        float z = 0.025F;
        ModelPart arm = this.getArmForSide(side);
        arm.x += x;
        arm.translateAndRotate(stack);
        stack.translate(z, y, z);
        arm.x -= x;
    }

    @Override
    public ModelPart getRightArm() {
        return this.rightArm;
    }

    @Override
    public ModelPart getLeftArm() {
        return this.leftArm;
    }

    @Override
    public ModelPart getRightWing() {
        return this.rightWing;
    }

    @Override
    public ModelPart getLeftWing() {
        return this.leftWing;
    }

    @Override
    public ModelPart getRightLeg() {
        return this.rightLeg;
    }

    @Override
    public ModelPart getLeftLeg() {
        return this.leftLeg;
    }

    @Override
    public ModelPart getHair() {
        return this.headOverlay;
    }

    @Override
    public ModelPart getBody() {
        return this.body;
    }

    @Override
    public ModelPart getTail() {
        return this.tailBase;
    }

    @Override
    public ModelPart getTailTip() {
        return this.tailTip;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    public static LayerDefinition create(CubeDeformation scale) {
        MeshDefinition parts = new MeshDefinition();
        PartDefinition root = parts.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("hair", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 20.0F, 8.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("right_bun", CubeListBuilder.create().texOffs(64, 9).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, scale), PartPose.offsetAndRotation(-4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("right_tail", CubeListBuilder.create().texOffs(64, 9).addBox(0.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, scale.extend(0.01F)), PartPose.offsetAndRotation(4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("left_bun", CubeListBuilder.create().texOffs(64, 0).addBox(0.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, scale), PartPose.offsetAndRotation(4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("left_tail", CubeListBuilder.create().texOffs(64, 0).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, scale.extend(0.01F)), PartPose.offsetAndRotation(-4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("back_bun", CubeListBuilder.create().texOffs(64, 18).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F, scale), PartPose.offsetAndRotation(0.0F, -5.0F, 4.0F, 0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("back_tail", CubeListBuilder.create().texOffs(64, 18).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 6.0F, 3.0F, scale), PartPose.offsetAndRotation(0.0F, -5.0F, 4.0F, 0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(16, 22).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F, scale), PartPose.offsetAndRotation(-3.0F, -8.0F, -0.5F, 0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(8, 22).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F, scale), PartPose.offsetAndRotation(3.0F, -8.0F, -0.5F, 0.0F, 0.0F, 0.0F));
        PartDefinition hat = head.addOrReplaceChild("hat_top", CubeListBuilder.create().texOffs(0, 40).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 8.0F, scale.extend(0.01F)), PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        hat.addOrReplaceChild("hat_brim", CubeListBuilder.create().texOffs(-16, 52).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, scale), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(6, 28).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, scale), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("bust", CubeListBuilder.create().texOffs(6, 28).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 4.0F, scale.extend(-0.01F)), PartPose.offsetAndRotation(0.0F, -2.9F, -2.0F, 0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(24, 28).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 8.0F, 12.0F, scale), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(104, 0).addBox(0.0F, -12.0F, 0.0F, 12.0F, 12.0F, 0.0F, scale), PartPose.offsetAndRotation(1.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(80, 0).addBox(-12.0F, -12.0F, 0.0F, 12.0F, 12.0F, 0.0F, scale), PartPose.offsetAndRotation(-1.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition tail = body.addOrReplaceChild("tail_base", CubeListBuilder.create().texOffs(11, 17).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F, scale), PartPose.offsetAndRotation(0.0F, 2.5F, 2.0F, 0.10471975511965977F, 0.0F, 0.0F));
        tail.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(10, 16).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 5.0F, scale), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.15707963267948966F, 0.0F, 0.0F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale), PartPose.offsetAndRotation(-4.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale), PartPose.offsetAndRotation(4.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(24, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale), PartPose.offsetAndRotation(-2.0F, 18.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(24, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale), PartPose.offsetAndRotation(2.0F, 18.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition headOverlay = root.addOrReplaceChild("head_overlay", CubeListBuilder.create().texOffs(0, 0 + 68).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        headOverlay.addOrReplaceChild("hair_overlay", CubeListBuilder.create().texOffs(32, 0 + 68).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 20.0F, 8.0F, scale.extend(1.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        headOverlay.addOrReplaceChild("right_bun_overlay", CubeListBuilder.create().texOffs(64, 9 + 68).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(-4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        headOverlay.addOrReplaceChild("right_tail_overlay", CubeListBuilder.create().texOffs(64, 9 + 68).addBox(0.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, scale.extend(0.49F)), PartPose.offsetAndRotation(4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        headOverlay.addOrReplaceChild("left_bun_overlay", CubeListBuilder.create().texOffs(64, 0 + 68).addBox(0.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        headOverlay.addOrReplaceChild("left_tail_overlay", CubeListBuilder.create().texOffs(64, 0 + 68).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, scale.extend(0.49F)), PartPose.offsetAndRotation(-4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        headOverlay.addOrReplaceChild("back_bun_overlay", CubeListBuilder.create().texOffs(64, 18 + 68).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(0.0F, -5.0F, 4.0F, 0.0F, 0.0F, 0.0F));
        headOverlay.addOrReplaceChild("back_tail_overlay", CubeListBuilder.create().texOffs(64, 18 + 68).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 6.0F, 3.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(0.0F, -5.0F, 4.0F, 0.0F, 0.0F, 0.0F));
        headOverlay.addOrReplaceChild("right_ear_overlay", CubeListBuilder.create().texOffs(16, 22 + 68).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(-3.0F, -8.0F, -0.5F, 0.0F, 0.0F, 0.0F));
        headOverlay.addOrReplaceChild("left_ear_overlay", CubeListBuilder.create().texOffs(8, 22 + 68).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(3.0F, -8.0F, -0.5F, 0.0F, 0.0F, 0.0F));
        PartDefinition bodyOverlay = root.addOrReplaceChild("body_overlay", CubeListBuilder.create().texOffs(6, 28 + 68).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        bodyOverlay.addOrReplaceChild("bust_overlay", CubeListBuilder.create().texOffs(6, 28 + 68).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 4.0F, scale.extend(0.49F)), PartPose.offsetAndRotation(0.0F, -2.9F, -2.0F, 0.0F, 0.0F, 0.0F));
        bodyOverlay.addOrReplaceChild("skirt_overlay", CubeListBuilder.create().texOffs(24, 28 + 68).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 8.0F, 12.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition tailOverlay = bodyOverlay.addOrReplaceChild("tail_base_overlay", CubeListBuilder.create().texOffs(11, 17 + 68).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(0.0F, 2.5F, 2.0F, 0.10471975511965977F, 0.0F, 0.0F));
        tailOverlay.addOrReplaceChild("tail_tip_overlay", CubeListBuilder.create().texOffs(10, 16 + 68).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 5.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("right_arm_overlay", CubeListBuilder.create().texOffs(0, 24 + 68).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(-4.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("left_arm_overlay", CubeListBuilder.create().texOffs(0, 16 + 68).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(4.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("right_leg_overlay", CubeListBuilder.create().texOffs(24, 24 + 68).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(-2.0F, 18.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        root.addOrReplaceChild("left_leg_overlay", CubeListBuilder.create().texOffs(24, 16 + 68).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(2.0F, 18.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(parts, 128, 128);
    }
}