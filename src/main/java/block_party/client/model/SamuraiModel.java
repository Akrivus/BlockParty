package block_party.client.model;

import block_party.client.BlockPartyRenderers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class SamuraiModel<T extends LivingEntity> extends HumanoidModel<T> {
    private final ModelPart leftArmOverlay;
    private final ModelPart rightArmOverlay;
    private final ModelPart leftLegOverlay;
    private final ModelPart rightLegOverlay;
    private final ModelPart bodyOverlay;

    public SamuraiModel(ModelPart root) {
        super(root, RenderType::entityTranslucent);
        this.leftArmOverlay = root.getChild("left_arm_overlay");
        this.rightArmOverlay = root.getChild("right_arm_overlay");
        this.leftLegOverlay = root.getChild("left_leg_overlay");
        this.rightLegOverlay = root.getChild("right_leg_overlay");
        this.bodyOverlay = root.getChild("body_overlay");
    }

    @Override
    public void setupAnim(T p_103395_, float p_103396_, float p_103397_, float p_103398_, float p_103399_, float p_103400_) {
        super.setupAnim(p_103395_, p_103396_, p_103397_, p_103398_, p_103399_, p_103400_);
        this.leftLegOverlay.copyFrom(this.leftLeg);
        this.rightLegOverlay.copyFrom(this.rightLeg);
        this.leftArmOverlay.copyFrom(this.leftArm);
        this.rightArmOverlay.copyFrom(this.rightArm);
        this.bodyOverlay.copyFrom(this.body);
    }

    public static LayerDefinition create(CubeDeformation scale) {
        MeshDefinition parts = HumanoidModel.createMesh(scale, 0.0F);
        PartDefinition root = parts.getRoot();
        root.addOrReplaceChild("left_arm_overlay", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale.extend(0.25F)), PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("right_arm_overlay", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale.extend(0.25F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_leg_overlay", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale.extend(0.25F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("right_leg_overlay", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale.extend(0.25F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("body_overlay", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, scale.extend(0.25F)), PartPose.ZERO);
        return LayerDefinition.create(parts, 64, 64);
    }

    private static SamuraiModel INNER_ARMOR_MODEL;
    private static SamuraiModel OUTER_ARMOR_MODEL;

    public static void setArmorModels(EntityRendererProvider.Context context) {
        SamuraiModel.INNER_ARMOR_MODEL = new SamuraiModel(context.bakeLayer(BlockPartyRenderers.SAMURAI_INNER_ARMOR));
        SamuraiModel.OUTER_ARMOR_MODEL = new SamuraiModel(context.bakeLayer(BlockPartyRenderers.SAMURAI_OUTER_ARMOR));
    }

    public static SamuraiModel getArmorModel(EquipmentSlot slot) {
        return slot == EquipmentSlot.LEGS ? SamuraiModel.INNER_ARMOR_MODEL : SamuraiModel.OUTER_ARMOR_MODEL;
    }
}
