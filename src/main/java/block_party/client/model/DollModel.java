package block_party.client.model;

import block_party.npc.BlockPartyNPC;
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

public class DollModel<T extends BlockPartyNPC> extends HierarchicalModel<T> implements ArmedModel, HeadedModel, IRiggableModel {
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
    private final ModelPart tiddies;
    private final ModelPart skirt;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart tailBase;
    private final ModelPart tailTip;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart headWear;
    private final ModelPart hairWear;
    private final ModelPart rightBunWear;
    private final ModelPart leftBunWear;
    private final ModelPart backBunWear;
    private final ModelPart rightTailWear;
    private final ModelPart leftTailWear;
    private final ModelPart backTailWear;
    private final ModelPart rightEarWear;
    private final ModelPart leftEarWear;
    private final ModelPart bodyWear;
    private final ModelPart tiddiesWear;
    private final ModelPart skirtWear;
    private final ModelPart tailBaseWear;
    private final ModelPart tailTipWear;
    private final ModelPart rightArmWear;
    private final ModelPart leftArmWear;
    private final ModelPart rightLegWear;
    private final ModelPart leftLegWear;

    public DollModel(ModelPart root) {
        this.root = root;
        this.head = this.root.getChild("head");
        this.hair = this.head.getChild("hair");
        this.rightBun = this.head.getChild("rightBun");
        this.rightTail = this.head.getChild("rightTail");
        this.leftBun = this.head.getChild("leftBun");
        this.leftTail = this.head.getChild("leftTail");
        this.backBun = this.head.getChild("backBun");
        this.backTail = this.head.getChild("backTail");
        this.rightEar = this.head.getChild("rightEar");
        this.leftEar = this.head.getChild("leftEar");
        this.hatTop = this.head.getChild("hatTop");
        this.hatBrim = this.hatTop.getChild("hatBrim");
        this.body = this.root.getChild("body");
        this.tiddies = this.body.getChild("tiddies");
        this.skirt = this.body.getChild("skirt");
        this.rightWing = this.body.getChild("rightWing");
        this.leftWing = this.body.getChild("leftWing");
        this.tailBase = this.body.getChild("tailBase");
        this.tailTip = this.tailBase.getChild("tailTip");
        this.rightArm = this.root.getChild("rightArm");
        this.leftArm = this.root.getChild("leftArm");
        this.rightLeg = this.root.getChild("rightLeg");
        this.leftLeg = this.root.getChild("leftLeg");
        this.headWear = this.root.getChild("headWear");
        this.hairWear = this.headWear.getChild("hairWear");
        this.rightBunWear = this.headWear.getChild("rightBunWear");
        this.rightTailWear = this.headWear.getChild("rightTailWear");
        this.leftBunWear = this.headWear.getChild("leftBunWear");
        this.leftTailWear = this.headWear.getChild("leftTailWear");
        this.backBunWear = this.headWear.getChild("backBunWear");
        this.backTailWear = this.headWear.getChild("backTailWear");
        this.rightEarWear = this.headWear.getChild("rightEarWear");
        this.leftEarWear = this.headWear.getChild("leftEarWear");
        this.bodyWear = this.root.getChild("bodyWear");
        this.tiddiesWear = this.bodyWear.getChild("tiddiesWear");
        this.skirtWear = this.bodyWear.getChild("skirtWear");
        this.tailBaseWear = this.bodyWear.getChild("tailBaseWear");
        this.tailTipWear = this.tailBaseWear.getChild("tailTipWear");
        this.rightArmWear = this.root.getChild("rightArmWear");
        this.leftArmWear = this.root.getChild("leftArmWear");
        this.rightLegWear = this.root.getChild("rightLegWear");
        this.leftLegWear = this.root.getChild("leftLegWear");
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
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
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float yaw, float pitch) {
        entity.getAnimation().setRotationAngles(entity, this, limbSwing, limbSwingAmount, ageInTicks);
        this.rightArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
        this.rightArm.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.leftArm.xRot += -Mth.sin(ageInTicks * 0.067F) * 0.05F;
        this.leftArm.zRot += -Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        this.head.xRot += pitch * 0.0174532925F;
        this.head.yRot += yaw * 0.0174532925F;
        this.headWear.copyFrom(this.head);
        this.hairWear.copyFrom(this.hair);
        this.rightBunWear.copyFrom(this.rightBun);
        this.leftBunWear.copyFrom(this.leftBun);
        this.backBunWear.copyFrom(this.backBun);
        this.rightTailWear.copyFrom(this.rightTail);
        this.leftTailWear.copyFrom(this.leftTail);
        this.backTailWear.copyFrom(this.backTail);
        this.rightEarWear.copyFrom(this.rightEar);
        this.leftEarWear.copyFrom(this.leftEar);
        this.bodyWear.copyFrom(this.body);
        this.tiddiesWear.copyFrom(this.tiddies);
        this.skirtWear.copyFrom(this.skirt);
        this.tailBaseWear.copyFrom(this.tailBase);
        this.tailTipWear.copyFrom(this.tailTip);
        this.rightArmWear.copyFrom(this.rightArm);
        this.leftArmWear.copyFrom(this.leftArm);
        this.rightLegWear.copyFrom(this.rightLeg);
        this.leftLegWear.copyFrom(this.leftLeg);
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
        this.rightWing.yRot = -(this.leftWing.yRot = entity.isOnGround() ? 0.0F : 0.47123894F + Mth.cos(entity.tickCount * partialTicks) * 3.14F * 0.05F);
        this.rightWing.zRot = -(this.leftWing.zRot = -0.23561947F);
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.rightLeg.yRot = this.rightLeg.zRot = 0.0F;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + 3.14F) * 1.4F * limbSwingAmount;
        this.leftLeg.yRot = this.leftLeg.zRot = 0.0F;
        this.tiddies.xRot = entity.getSlouch() * -0.024240684F;
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
    public ModelPart root() {
        return this.root;
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
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public ModelPart getHair() {
        return this.headWear;
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

    public static LayerDefinition create() {
        MeshDefinition parts = new MeshDefinition();
        PartDefinition root = parts.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition hair = head.addOrReplaceChild("hair", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 20.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition rightBun = head.addOrReplaceChild("rightBun", CubeListBuilder.create().texOffs(64, 9).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F), PartPose.offsetAndRotation(-4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition rightTail = head.addOrReplaceChild("rightTail", CubeListBuilder.create().texOffs(64, 9).addBox(0.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition leftBun = head.addOrReplaceChild("leftBun", CubeListBuilder.create().texOffs(64, 0).addBox(0.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F), PartPose.offsetAndRotation(4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition leftTail = head.addOrReplaceChild("leftTail", CubeListBuilder.create().texOffs(64, 0).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition backBun = head.addOrReplaceChild("backBun", CubeListBuilder.create().texOffs(64, 18).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -5.0F, 4.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition backTail = head.addOrReplaceChild("backTail", CubeListBuilder.create().texOffs(64, 18).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 6.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -5.0F, 4.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition rightEar = head.addOrReplaceChild("rightEar", CubeListBuilder.create().texOffs(16, 22).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F), PartPose.offsetAndRotation(-3.0F, -8.0F, -0.5F, 0.0F, 0.0F, 0.0F));
        PartDefinition leftEar = head.addOrReplaceChild("leftEar", CubeListBuilder.create().texOffs(8, 22).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F), PartPose.offsetAndRotation(3.0F, -8.0F, -0.5F, 0.0F, 0.0F, 0.0F));
        PartDefinition hatTop = head.addOrReplaceChild("hatTop", CubeListBuilder.create().texOffs(0, 40).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition hatBrim = hatTop.addOrReplaceChild("hatBrim", CubeListBuilder.create().texOffs(-16, 52).addBox(-8.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(6, 28).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition tiddies = body.addOrReplaceChild("tiddies", CubeListBuilder.create().texOffs(6, 28).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, -2.9F, -2.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition skirt = body.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(24, 28).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 8.0F, 12.0F), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition rightWing = body.addOrReplaceChild("rightWing", CubeListBuilder.create().texOffs(104, 0).addBox(0.0F, -12.0F, 0.0F, 12.0F, 12.0F, 0.0F), PartPose.offsetAndRotation(1.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition leftWing = body.addOrReplaceChild("leftWing", CubeListBuilder.create().texOffs(80, 0).addBox(-12.0F, -12.0F, 0.0F, 12.0F, 12.0F, 0.0F), PartPose.offsetAndRotation(-1.0F, 0.0F, 2.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition tailBase = body.addOrReplaceChild("tailBase", CubeListBuilder.create().texOffs(11, 17).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 2.5F, 2.0F, 0.10471975511965977F, 0.0F, 0.0F));
        PartDefinition tailTip = tailBase.addOrReplaceChild("tailTip", CubeListBuilder.create().texOffs(10, 16).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 5.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.15707963267948966F, 0.0F, 0.0F));
        PartDefinition rightArm = root.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(-4.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition leftArm = root.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(4.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition rightLeg = root.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(24, 24).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(-2.0F, 18.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition leftLeg = root.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(24, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F), PartPose.offsetAndRotation(2.0F, 18.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition headWear = root.addOrReplaceChild("headWear", CubeListBuilder.create().texOffs(0, 0 + 68).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition hairWear = headWear.addOrReplaceChild("hairWear", CubeListBuilder.create().texOffs(32, 0 + 68).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 20.0F, 8.0F, new CubeDeformation(0.5F + 0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition rightBunWear = headWear.addOrReplaceChild("rightBunWear", CubeListBuilder.create().texOffs(64, 9 + 68).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition rightTailWear = headWear.addOrReplaceChild("rightTailWear", CubeListBuilder.create().texOffs(64, 9 + 68).addBox(0.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.5F + 0.01F)), PartPose.offsetAndRotation(4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition leftBunWear = headWear.addOrReplaceChild("leftBunWear", CubeListBuilder.create().texOffs(64, 0 + 68).addBox(0.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition leftTailWear = headWear.addOrReplaceChild("leftTailWear", CubeListBuilder.create().texOffs(64, 0 + 68).addBox(-3.0F, -1.5F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.5F + 0.01F)), PartPose.offsetAndRotation(-4.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition backBunWear = headWear.addOrReplaceChild("backBunWear", CubeListBuilder.create().texOffs(64, 18 + 68).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -5.0F, 4.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition backTailWear = headWear.addOrReplaceChild("backTailWear", CubeListBuilder.create().texOffs(64, 18 + 68).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, -5.0F, 4.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition rightEarWear = headWear.addOrReplaceChild("rightEarWear", CubeListBuilder.create().texOffs(16, 22 + 68).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-3.0F, -8.0F, -0.5F, 0.0F, 0.0F, 0.0F));
        PartDefinition leftEarWear = headWear.addOrReplaceChild("leftEarWear", CubeListBuilder.create().texOffs(8, 22 + 68).addBox(-0.5F, -2.0F, -1.5F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(3.0F, -8.0F, -0.5F, 0.0F, 0.0F, 0.0F));
        PartDefinition bodyWear = root.addOrReplaceChild("bodyWear", CubeListBuilder.create().texOffs(6, 28 + 68).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition tiddiesWear = bodyWear.addOrReplaceChild("tiddiesWear", CubeListBuilder.create().texOffs(6, 28 + 68).addBox(-3.0F, 0.0F, 0.0F, 6.0F, 3.0F, 4.0F, new CubeDeformation(0.49F)), PartPose.offsetAndRotation(0.0F, -2.9F, -2.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition skirtWear = bodyWear.addOrReplaceChild("skirtWear", CubeListBuilder.create().texOffs(24, 28 + 68).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition tailBaseWear = bodyWear.addOrReplaceChild("tailBaseWear", CubeListBuilder.create().texOffs(11, 17 + 68).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 2.5F, 2.0F, 0.10471975511965977F, 0.0F, 0.0F));
        PartDefinition tailTipWear = tailBaseWear.addOrReplaceChild("tailTipWear", CubeListBuilder.create().texOffs(10, 16 + 68).addBox(-0.5F, -0.5F, 0.0F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition rightArmWear = root.addOrReplaceChild("rightArmWear", CubeListBuilder.create().texOffs(0, 24 + 68).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-4.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition leftArmWear = root.addOrReplaceChild("leftArmWear", CubeListBuilder.create().texOffs(0, 16 + 68).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(4.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition rightLegWear = root.addOrReplaceChild("rightLegWear", CubeListBuilder.create().texOffs(24, 24 + 68).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(-2.0F, 18.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition leftLegWear = root.addOrReplaceChild("leftLegWear", CubeListBuilder.create().texOffs(24, 16 + 68).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.5F)), PartPose.offsetAndRotation(2.0F, 18.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        return LayerDefinition.create(parts, 128, 128);
    }
}