package block_party.client.model;

import block_party.client.renderers.state.MoeRenderState;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

public class MoeModel extends EntityModel<MoeRenderState> implements ArmedModel, HeadedModel, IRiggableModel {
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
        super(root);
        this.head = root.getChild("head");
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
        this.body = root.getChild("body");
        this.bust = this.body.getChild("bust");
        this.skirt = this.body.getChild("skirt");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
        this.tailBase = this.body.getChild("tail_base");
        this.tailTip = this.tailBase.getChild("tail_tip");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.headOverlay = root.getChild("head_overlay");
        this.hairOverlay = this.headOverlay.getChild("hair_overlay");
        this.rightBunOverlay = this.headOverlay.getChild("right_bun_overlay");
        this.rightTailOverlay = this.headOverlay.getChild("right_tail_overlay");
        this.leftBunOverlay = this.headOverlay.getChild("left_bun_overlay");
        this.leftTailOverlay = this.headOverlay.getChild("left_tail_overlay");
        this.backBunOverlay = this.headOverlay.getChild("back_bun_overlay");
        this.backTailOverlay = this.headOverlay.getChild("back_tail_overlay");
        this.rightEarOverlay = this.headOverlay.getChild("right_ear_overlay");
        this.leftEarOverlay = this.headOverlay.getChild("left_ear_overlay");
        this.bodyOverlay = root.getChild("body_overlay");
        this.bustOverlay = this.bodyOverlay.getChild("bust_overlay");
        this.skirtOverlay = this.bodyOverlay.getChild("skirt_overlay");
        this.tailBaseOverlay = this.bodyOverlay.getChild("tail_base_overlay");
        this.tailTipOverlay = this.tailBaseOverlay.getChild("tail_tip_overlay");
        this.rightArmOverlay = root.getChild("right_arm_overlay");
        this.leftArmOverlay = root.getChild("left_arm_overlay");
        this.rightLegOverlay = root.getChild("right_leg_overlay");
        this.leftLegOverlay = root.getChild("left_leg_overlay");
    }

    @Override
    public void setupAnim(MoeRenderState state) {
        super.setupAnim(state);
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float limbSwing = state.walkAnimationPos;
        float limbSwingAmount = state.walkAnimationSpeed;
        float ageInTicks = state.ageInTicks;

        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * limbSwingAmount;
        this.rightArm.zRot = 0.7853982F + Mth.cos(ageInTicks * 0.09F) * 0.05F;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
        this.leftArm.zRot = -0.7853982F - Mth.cos(ageInTicks * 0.09F) * 0.05F;
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;
        this.head.xRot = state.xRot * Mth.DEG_TO_RAD;
        this.head.yRot = state.yRot * Mth.DEG_TO_RAD;
        this.bust.xRot = state.slouch * -0.024240684F;
        this.skirt.yRot = -this.leftLeg.xRot * 0.25F;
        this.tailBase.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
        this.tailBase.zRot = -this.tailBase.xRot * 0.5F - Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.rightWing.visible = state.hasWings;
        this.leftWing.visible = state.hasWings;
        this.rightEar.visible = state.hasCatFeatures;
        this.leftEar.visible = state.hasCatFeatures;
        this.rightEarOverlay.visible = state.hasCatFeatures;
        this.leftEarOverlay.visible = state.hasCatFeatures;
        this.tailBase.visible = state.hasCatFeatures;
        this.tailTip.visible = state.hasCatFeatures;
        this.tailBaseOverlay.visible = state.hasCatFeatures;
        this.tailTipOverlay.visible = state.hasCatFeatures;

        if (state.isCrouching) {
            this.head.y = 14.2F;
            this.body.y = 16.2F;
            this.body.xRot = 0.5F;
            this.rightArm.y = 15.2F;
            this.leftArm.y = 15.2F;
            this.rightLeg.y = 19.2F;
            this.rightLeg.z = 1.0F;
            this.leftLeg.y = 19.2F;
            this.leftLeg.z = 1.0F;
        }

        copyOverlayParts();
    }

    private void copyOverlayParts() {
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
    public void translateToHand(HumanoidArm side, com.mojang.blaze3d.vertex.PoseStack stack) {
        ModelPart arm = this.getArmForSide(side);
        arm.translateAndRotate(stack);
        stack.translate(side == HumanoidArm.RIGHT ? 0.0625F : -0.0625F, -0.15F, 0.025F);
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
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale), PartPose.offset(0.0F, 12.0F, 0.0F));
        head.addOrReplaceChild("hair", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 20.0F, 8.0F, scale.extend(0.5F)), PartPose.ZERO);
        head.addOrReplaceChild("right_bun", CubeListBuilder.create().texOffs(64, 9).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, scale), PartPose.offset(-4.0F, -5.0F, 0.0F));
        head.addOrReplaceChild("right_tail", CubeListBuilder.create().texOffs(64, 9).addBox(0.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, scale.extend(0.01F)), PartPose.offset(4.0F, -5.0F, 0.0F));
        head.addOrReplaceChild("left_bun", CubeListBuilder.create().texOffs(64, 0).addBox(0.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, scale), PartPose.offset(4.0F, -5.0F, 0.0F));
        head.addOrReplaceChild("left_tail", CubeListBuilder.create().texOffs(64, 0).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, scale.extend(0.01F)), PartPose.offset(-4.0F, -5.0F, 0.0F));
        head.addOrReplaceChild("back_bun", CubeListBuilder.create().texOffs(64, 18).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F, scale), PartPose.offset(0.0F, -5.0F, 4.0F));
        head.addOrReplaceChild("back_tail", CubeListBuilder.create().texOffs(64, 18).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 6.0F, 3.0F, scale), PartPose.offset(0.0F, -5.0F, 4.0F));
        head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(16, 22).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F, scale), PartPose.offset(-3.0F, -8.0F, -0.5F));
        head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(8, 22).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F, scale), PartPose.offset(3.0F, -8.0F, -0.5F));
        PartDefinition hat = head.addOrReplaceChild("hat_top", CubeListBuilder.create().texOffs(0, 40).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 8.0F, scale.extend(0.01F)), PartPose.offset(0.0F, -7.0F, 0.0F));
        hat.addOrReplaceChild("hat_brim", CubeListBuilder.create().texOffs(-16, 52).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, scale), PartPose.ZERO);
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(6, 28).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, scale), PartPose.offset(0.0F, 15.0F, 0.0F));
        body.addOrReplaceChild("bust", CubeListBuilder.create().texOffs(6, 28).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 4.0F, scale.extend(-0.01F)), PartPose.offset(0.0F, -2.9F, -2.0F));
        body.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(24, 28).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 8.0F, 12.0F, scale), PartPose.offset(0.0F, 1.0F, 0.0F));
        body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(104, 0).addBox(0.0F, -12.0F, 0.0F, 12.0F, 12.0F, 0.0F, scale), PartPose.offsetAndRotation(1.0F, 0.0F, 2.0F, -0.23561947F, 0.0F, 0.23561947F));
        body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(80, 0).addBox(-12.0F, -12.0F, 0.0F, 12.0F, 12.0F, 0.0F, scale), PartPose.offsetAndRotation(-1.0F, 0.0F, 2.0F, -0.23561947F, 0.0F, -0.23561947F));
        PartDefinition tail = body.addOrReplaceChild("tail_base", CubeListBuilder.create().texOffs(11, 17).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F, scale), PartPose.offsetAndRotation(0.0F, 2.5F, 2.0F, 0.10471976F, 0.0F, 0.0F));
        tail.addOrReplaceChild("tail_tip", CubeListBuilder.create().texOffs(10, 16).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 5.0F, scale), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.15707964F, 0.0F, 0.0F));
        root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale), PartPose.offset(-4.0F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale), PartPose.offset(4.0F, 12.0F, 0.0F));
        root.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(24, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale), PartPose.offset(-2.0F, 18.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(24, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale), PartPose.offset(2.0F, 18.0F, 0.0F));
        addOverlayParts(root, scale);
        return LayerDefinition.create(parts, 128, 128);
    }

    private static void addOverlayParts(PartDefinition root, CubeDeformation scale) {
        PartDefinition head = root.addOrReplaceChild("head_overlay", CubeListBuilder.create().texOffs(0, 68).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale.extend(0.5F)), PartPose.offset(0.0F, 12.0F, 0.0F));
        head.addOrReplaceChild("hair_overlay", CubeListBuilder.create().texOffs(32, 68).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 20.0F, 8.0F, scale.extend(1.0F)), PartPose.ZERO);
        head.addOrReplaceChild("right_bun_overlay", CubeListBuilder.create().texOffs(64, 77).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, scale.extend(0.5F)), PartPose.offset(-4.0F, -5.0F, 0.0F));
        head.addOrReplaceChild("right_tail_overlay", CubeListBuilder.create().texOffs(64, 77).addBox(0.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, scale.extend(0.49F)), PartPose.offset(4.0F, -5.0F, 0.0F));
        head.addOrReplaceChild("left_bun_overlay", CubeListBuilder.create().texOffs(64, 68).addBox(0.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, scale.extend(0.5F)), PartPose.offset(4.0F, -5.0F, 0.0F));
        head.addOrReplaceChild("left_tail_overlay", CubeListBuilder.create().texOffs(64, 68).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, scale.extend(0.49F)), PartPose.offset(-4.0F, -5.0F, 0.0F));
        head.addOrReplaceChild("back_bun_overlay", CubeListBuilder.create().texOffs(64, 86).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F, scale.extend(0.5F)), PartPose.offset(0.0F, -5.0F, 4.0F));
        head.addOrReplaceChild("back_tail_overlay", CubeListBuilder.create().texOffs(64, 86).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 6.0F, 3.0F, scale.extend(0.5F)), PartPose.offset(0.0F, -5.0F, 4.0F));
        head.addOrReplaceChild("right_ear_overlay", CubeListBuilder.create().texOffs(16, 90).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F, scale.extend(0.5F)), PartPose.offset(-3.0F, -8.0F, -0.5F));
        head.addOrReplaceChild("left_ear_overlay", CubeListBuilder.create().texOffs(8, 90).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F, scale.extend(0.5F)), PartPose.offset(3.0F, -8.0F, -0.5F));
        PartDefinition body = root.addOrReplaceChild("body_overlay", CubeListBuilder.create().texOffs(6, 96).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, scale.extend(0.5F)), PartPose.offset(0.0F, 15.0F, 0.0F));
        body.addOrReplaceChild("bust_overlay", CubeListBuilder.create().texOffs(6, 96).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 4.0F, scale.extend(0.49F)), PartPose.offset(0.0F, -2.9F, -2.0F));
        body.addOrReplaceChild("skirt_overlay", CubeListBuilder.create().texOffs(24, 96).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 8.0F, 12.0F, scale.extend(0.5F)), PartPose.offset(0.0F, 1.0F, 0.0F));
        PartDefinition tail = body.addOrReplaceChild("tail_base_overlay", CubeListBuilder.create().texOffs(11, 85).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F, scale.extend(0.5F)), PartPose.offsetAndRotation(0.0F, 2.5F, 2.0F, 0.10471976F, 0.0F, 0.0F));
        tail.addOrReplaceChild("tail_tip_overlay", CubeListBuilder.create().texOffs(10, 84).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 5.0F, scale.extend(0.5F)), PartPose.offset(0.0F, 0.0F, 4.0F));
        root.addOrReplaceChild("right_arm_overlay", CubeListBuilder.create().texOffs(0, 92).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale.extend(0.5F)), PartPose.offset(-4.0F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_arm_overlay", CubeListBuilder.create().texOffs(0, 84).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale.extend(0.5F)), PartPose.offset(4.0F, 12.0F, 0.0F));
        root.addOrReplaceChild("right_leg_overlay", CubeListBuilder.create().texOffs(24, 92).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale.extend(0.5F)), PartPose.offset(-2.0F, 18.0F, 0.0F));
        root.addOrReplaceChild("left_leg_overlay", CubeListBuilder.create().texOffs(24, 84).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, scale.extend(0.5F)), PartPose.offset(2.0F, 18.0F, 0.0F));
    }
}
