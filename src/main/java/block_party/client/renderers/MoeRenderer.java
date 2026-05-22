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
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Matrix4f;

public class MoeRenderer extends MobRenderer<Moe, MoeRenderState, MoeModel> {
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
        state.moeScale = moe.getMoeScale();
        state.slouch = moe.getSlouch();
        state.health = moe.getHealth();
        state.maxHealth = moe.getMaxHealth();
        state.mainArm = moe.getMainArm();
        state.attackArm = moe.swingingArm == net.minecraft.world.InteractionHand.OFF_HAND ? moe.getMainArm().getOpposite() : moe.getMainArm();
        state.isCrouching = moe.isShiftKeyDown();
        state.rightArmPose = net.minecraft.client.model.HumanoidModel.ArmPose.EMPTY;
        state.leftArmPose = net.minecraft.client.model.HumanoidModel.ArmPose.EMPTY;
        fillEyeColor(moe, state.eyeColor);
    }

    @Override
    public ResourceLocation getTextureLocation(MoeRenderState state) {
        return state.texture == null ? BlockParty.source("textures/moe/air.png") : state.texture;
    }

    @Override
    protected void renderNameTag(MoeRenderState state, Component name, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        if (state.distanceToCameraSq > 64.0D) {
            return;
        }
        String[] lines = new String[] { String.format("%d / %d", (int) state.health, (int) state.maxHealth), name.getString() };
        stack.pushPose();
        stack.translate(0.0D, state.boundingBoxHeight + 0.5F, 0.0D);
        stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        stack.scale(-0.025F, -0.025F, 0.025F);
        for (int index = 0; index < lines.length; ++index) {
            Font font = this.getFont();
            int x = -font.width(lines[index]) / 2;
            int y = index * -10;
            Matrix4f matrix = stack.last().pose();
            int alpha = (int) (Minecraft.getInstance().options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
            font.drawInBatch(lines[index], x, y, 0xFFFFFFFF, false, matrix, buffer, Font.DisplayMode.NORMAL, alpha, packedLight);
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
