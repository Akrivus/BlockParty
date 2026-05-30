package block_party.client.renderers;

import block_party.BlockParty;
import block_party.client.BlockPartyRenderers;
import block_party.client.model.MoeModel;
import block_party.client.renderers.layers.EmoteLayer;
import block_party.client.renderers.layers.GlowLayer;
import block_party.client.renderers.layers.SpecialLayer;
import block_party.client.renderers.state.MoeRenderState;
import block_party.entities.Moe;
import block_party.registry.resources.MoeTextures;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class MoeRenderer extends MobRenderer<Moe, MoeRenderState, MoeModel> {
    private static final double NAMEPLATE_DISTANCE_SQ = 64.0D;

    public MoeRenderer(EntityRendererProvider.Context context) {
        super(context, new MoeModel(context.bakeLayer(BlockPartyRenderers.MOE)), 0.25F);
        this.addLayer(new ItemInHandLayer<>(this));
        this.addLayer(new EmoteLayer(this));
        this.addLayer(new GlowLayer(this));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet()));
        this.addLayer(new SpecialLayer(this));
    }

    @Override
    public MoeRenderState createRenderState() {
        return new MoeRenderState();
    }

    @Override
    public void extractRenderState(Moe moe, MoeRenderState state, float partialTick) {
        super.extractRenderState(moe, state, partialTick);
        state.texture = MoeTextures.get(moe);
        state.eyeTexture = BlockParty.source("textures/moe/emotions/" + moe.getEmotion().toLowerCase() + ".eyes.png");
        state.faceTexture = BlockParty.source("textures/moe/emotions/" + moe.getEmotion().toLowerCase() + ".png");
        ResourceLocation block = BuiltInRegistries.BLOCK.getKey(moe.getVisibleBlockState().getBlock());
        state.glowTexture = ResourceLocation.fromNamespaceAndPath(block.getNamespace(), "textures/moe/" + block.getPath() + ".glow.png");
        state.visibleBlockState = moe.getVisibleBlockState();
        state.hasWings = moe.hasWings();
        state.hasCatFeatures = moe.hasCatFeatures();
        state.hasGlow = moe.hasGlow();
        state.guiPreview = moe.isGuiPreview();
        state.onGround = moe.onGround();
        state.isSitting = moe.isSitting();
        state.animation = moe.getAnimationKey();
        state.moeScale = moe.getMoeScale();
        state.slouch = moe.getSlouch();
        state.health = moe.getHealth();
        state.maxHealth = moe.getMaxHealth();
        state.mainArm = moe.getMainArm();
        state.attackArm = moe.swingingArm == InteractionHand.OFF_HAND ? moe.getMainArm().getOpposite() : moe.getMainArm();
        state.isCrouching = moe.isShiftKeyDown();
        state.rightArmPose = ArmPose.EMPTY;
        state.leftArmPose = ArmPose.EMPTY;
        state.nameTag = moe.getDisplayName();
        state.nameTagAttachment = moe.getAttachments().getNullable(EntityAttachment.NAME_TAG, 0, moe.getYRot(partialTick));
        fillEyeColor(moe, state.eyeColor);
    }

    @Override
    public ResourceLocation getTextureLocation(MoeRenderState state) {
        return state.texture == null ? BlockParty.source("textures/moe/air.png") : state.texture;
    }

    @Override
    protected boolean shouldShowName(Moe moe, double distanceToCameraSq) {
        return distanceToCameraSq <= NAMEPLATE_DISTANCE_SQ || super.shouldShowName(moe, distanceToCameraSq);
    }

    @Override
    protected void renderNameTag(MoeRenderState state, Component name, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        if (state.distanceToCameraSq > NAMEPLATE_DISTANCE_SQ) {
            return;
        }
        Vec3 attachment = state.nameTagAttachment;
        if (attachment == null) {
            return;
        }
        Component[] lines = new Component[] { Component.literal(String.format("%d / %d", (int) state.health, (int) state.maxHealth)), name };
        boolean seeThrough = !state.isDiscrete;
        stack.pushPose();
        stack.translate(attachment.x, attachment.y + 0.5D, attachment.z);
        stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        stack.scale(0.025F, -0.025F, 0.025F);
        Matrix4f matrix = stack.last().pose();
        Font font = this.getFont();
        int background = (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
        for (int index = 0; index < lines.length; ++index) {
            Component line = lines[index];
            float x = (float) (-font.width(line)) / 2.0F;
            float y = (float) (index * 10);
            font.drawInBatch(line, x, y, -2130706433, false, matrix, buffer, seeThrough ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, background, packedLight);
            if (seeThrough) {
                font.drawInBatch(line, x, y, -1, false, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.lightCoordsWithEmission(packedLight, 2));
            }
        }
        stack.popPose();
    }

    @Override
    protected void scale(MoeRenderState state, PoseStack stack) {
        super.scale(state, stack);
        stack.scale(state.moeScale, state.moeScale, state.moeScale);
    }

    @Override
    protected float getShadowRadius(MoeRenderState state) {
        return 0.25F * state.moeScale;
    }

    private static void fillEyeColor(Moe moe, float[] target) {
        int color = BuiltInRegistries.BLOCK.getKey(moe.getActualBlockState().getBlock()).hashCode();
        int rgb = ARGB.opaque(color);
        target[0] = Math.max(0.25F, ARGB.redFloat(rgb));
        target[1] = Math.max(0.25F, ARGB.greenFloat(rgb));
        target[2] = Math.max(0.25F, ARGB.blueFloat(rgb));
    }
}
