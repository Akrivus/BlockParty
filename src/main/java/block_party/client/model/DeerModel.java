package block_party.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class DeerModel<T extends Deer> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart neckJoint;
    private final ModelPart head;
    private final ModelPart rightAntler;
    private final ModelPart leftAntler;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart snout;
    private final ModelPart frontLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart backLeftLeg;
    private final ModelPart backLeftLegTop;
    private final ModelPart backLeftLegBottom;
    private final ModelPart backRightLeg;
    private final ModelPart backRightLegTop;
    private final ModelPart backRightLegBottom;
    private final ModelPart tail;

    public DeerModel(ModelPart root) {
        this.root = root;
        this.body = this.root.getChild("body");
        this.neck = this.body.getChild("neck");
        this.neckJoint = this.neck.getChild("neckJoint");
        this.head = this.neckJoint.getChild("head");
        this.leftAntler = this.head.getChild("leftAntler");
        this.rightAntler = this.head.getChild("rightAntler");
        this.leftEar = this.head.getChild("leftEar");
        this.rightEar = this.head.getChild("rightEar");
        this.snout = this.head.getChild("snout");
        this.frontLeftLeg = this.body.getChild("frontLeftLeg");
        this.frontRightLeg = this.body.getChild("frontRightLeg");
        this.backLeftLeg = this.body.getChild("backLeftLeg");
        this.backLeftLegTop = this.backLeftLeg.getChild("backLeftLegTop");
        this.backLeftLegBottom = this.backLeftLegTop.getChild("backLeftLegBottom");
        this.backRightLeg = this.body.getChild("backRightLeg");
        this.backRightLegTop = this.backRightLeg.getChild("backRightLegTop");
        this.backRightLegBottom = this.backRightLegTop.getChild("backRightLegBottom");
        this.tail = this.body.getChild("tail");
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
        this.body.render(stack, buffer, light, overlay, red, green, blue, alpha);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition parts = new MeshDefinition();
        PartDefinition root = parts.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, 0.0F, 6, 8, 12, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.0F, -8.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(24, 8).addBox(-2.0F, -2.0F, -9.0F, 4, 4, 12, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.3F, 1.0F, -1.2747884856566583F, 0.0F, 0.0F));
        PartDefinition neckJoint = neck.addOrReplaceChild("neckJoint", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -9.0F, 1.4114477660878142F, 0.0F, 0.0F));
        PartDefinition head = neckJoint.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 20).addBox(-2.5F, -2.0F, -3.0F, 5, 4, 5, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.012735471695871659F, 0.0F));
        PartDefinition leftAntler = head.addOrReplaceChild("leftAntler", CubeListBuilder.create().texOffs(0, 34).addBox(-12.0F, -12.0F, 0.0F, 12, 12, 0, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3490658503988659F, 0.5235987755982988F, -0.3490658503988659F));
        PartDefinition rightAntler = head.addOrReplaceChild("rightAntler", CubeListBuilder.create().texOffs(0, 34).addBox(0.0F, -12.0F, 0.0F, 12, 12, 0, new CubeDeformation(0.0F)).mirror(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.3490658503988659F, -0.5235987755982988F, -0.3490658503988659F));
        PartDefinition leftEar = head.addOrReplaceChild("leftEar", CubeListBuilder.create().texOffs(0, 29).addBox(0.0F, -4.0F, 0.0F, 4, 4, 0, new CubeDeformation(0.0F)).mirror(), PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, -0.17453292519943295F, 0.3490658503988659F, 0.0F));
        PartDefinition rightEar = head.addOrReplaceChild("rightEar", CubeListBuilder.create().texOffs(0, 29).addBox(-4.0F, -4.0F, 0.0F, 4, 4, 0, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, -0.17453292519943295F, 0.0F, -0.3490658503988659F));
        PartDefinition snout = head.addOrReplaceChild("snout", CubeListBuilder.create().texOffs(32, 0).addBox(-1.5F, -1.0F, -2.0F, 3, 3, 3, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition frontLeftLeg = body.addOrReplaceChild("frontLeftLeg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition frontRightLeg = body.addOrReplaceChild("frontRightLeg", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, 0.0F, 2, 10, 2, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 6.0F, 1.0F, 0.0F, 0.0F, 0.0F));
        PartDefinition backLeftLeg = body.addOrReplaceChild("backLeftLeg", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, 6.0F, 9.2F, 0.0F, 0.0F, 0.0F));
        PartDefinition backLeftLegTop = backLeftLeg.addOrReplaceChild("backLeftLegTop", CubeListBuilder.create().texOffs(44, 9).addBox(-2.0F, -2.0F, -0.7F, 3, 6, 4, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7853981633974483F, 0.0F, 0.0F));
        PartDefinition backLeftLegBottom = backLeftLegTop.addOrReplaceChild("backLeftLegBottom", CubeListBuilder.create().texOffs(54, 22).addBox(-1.0F, -1.0F, 0.0F, 2, 9, 2, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 4.0F, 0.9F, -0.6829473363053812F, 0.0F, 0.0F));
        PartDefinition backRightLeg = body.addOrReplaceChild("backRightLeg", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, 6.0F, 9.2F, 0.0F, 0.0F, 0.0F));
        PartDefinition backRightLegTop = backRightLeg.addOrReplaceChild("backRightLegTop", CubeListBuilder.create().texOffs(44, 9).addBox(-1.0F, -2.0F, -0.7F, 3, 6, 4, new CubeDeformation(0.0F)).mirror(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7853981633974483F, 0.0F, 0.0F));
        PartDefinition backRightLegBottom = backRightLegTop.addOrReplaceChild("backRightLegBottom", CubeListBuilder.create().texOffs(54, 22).addBox(-1.0F, -1.0F, 0.0F, 2, 9, 2, new CubeDeformation(0.0F)).mirror(), PartPose.offsetAndRotation(0.5F, 4.0F, 0.9F, -0.6829473363053812F, 0.0F, 0.0F));
        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(44, 0).addBox(-1.5F, 0.0F, -3.0F, 3, 6, 3, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 12.0F, 0.5009094953223726F, 0.0F, 0.0F));
        return LayerDefinition.create(parts, 64, 64);
    }
}