package block_party.client.skybox;

import block_party.BlockParty;
import block_party.client.ShrineLocation;
import block_party.registry.CustomParticles;
import block_party.registry.CustomTags;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.mojang.math.Axis;

public final class JapanRenderer {
    private static final ResourceLocation FUJI_TEXTURE = BlockParty.source("textures/misc/fuji.png");
    private static final int SIZE = 288;
    private static int horizon;
    private static float distance = Float.POSITIVE_INFINITY;
    private static float bearing;
    private static float red;
    private static float green;
    private static float blue;
    private static float factor;

    private JapanRenderer() {
    }

    public static void renderFuji(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY || factor <= 0.0F) {
            return;
        }
        PoseStack stack = event.getPoseStack();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        stack.pushPose();
        stack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        stack.mulPose(Axis.YP.rotationDegrees(180.0F));
        stack.mulPose(Axis.ZP.rotationDegrees(bearing - 90.0F));
        stack.translate(0.0D, 0.0D, -156.0D);
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        RenderSystem.setShaderColor(red, green, blue, factor);
        RenderSystem.setShaderTexture(0, FUJI_TEXTURE);
        Matrix4f matrix = stack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -SIZE / 2.0F, horizon, -SIZE / 2.0F).setUv(0.0F, 0.0F);
        buffer.addVertex(matrix, SIZE / 2.0F, horizon, -SIZE / 2.0F).setUv(1.0F, 0.0F);
        buffer.addVertex(matrix, SIZE / 2.0F, horizon, SIZE / 2.0F).setUv(1.0F, 1.0F);
        buffer.addVertex(matrix, -SIZE / 2.0F, horizon, SIZE / 2.0F).setUv(0.0F, 1.0F);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
        stack.popPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void addFireflies(PlayerTickEvent.Post event) {
        Level level = event.getEntity().level();
        if (!level.isClientSide() || factor < 0.8F) {
            return;
        }
        long time = level.getDayTime() % 24000L;
        if (time > 21500L || time < 12500L) {
            return;
        }
        for (int generation = 0; generation < 3; ++generation) {
            float x = level.random.nextInt(128) - 64;
            float z = level.random.nextInt(128) - 64;
            for (int y = 0; y > event.getEntity().getY() - 255; --y) {
                BlockPos pos = event.getEntity().blockPosition().offset((int) x, y, (int) z);
                Vec3 particlePos = event.getEntity().position().add(new Vec3(x, y, z));
                Vector3f vec = particlePos.toVector3f();
                if (level.getBlockState(pos).is(CustomTags.SPAWNS_FIREFLIES)) {
                    level.addParticle(CustomParticles.FIREFLY.get(), vec.x, pos.getY(), vec.z, bearing, distance, factor);
                    break;
                }
            }
        }
    }

    public static void tintFog(ViewportEvent.ComputeFogColor event) {
        LocalPlayer player = player();
        if (player == null) {
            factor = 0.0F;
            return;
        }
        horizon = Minecraft.getInstance().options.renderDistance().get() * 16;
        Optional<BlockPos> shrine = ShrineLocation.get(player.blockPosition());
        if (shrine.isPresent()) {
            BlockPos pos = shrine.get();
            float x = player.getBlockX() - pos.getX();
            float z = player.getBlockZ() - pos.getZ();
            bearing = (float) (Math.atan2(z, x) * 180.0D / Math.PI);
            distance = Math.abs(x) + Math.abs(z);
            factor = (float) (-Math.sqrt(distance / 2048.0F - 0.03125F) + 1.0F);
            if (distance < 64.0F) {
                factor = 1.0F;
            }
            if (distance > 2048.0F) {
                factor = 0.0F;
            }
        } else {
            distance = Float.POSITIVE_INFINITY;
            factor = 0.0F;
        }
        event.setRed(red = ((0.95F - event.getRed()) * factor * 0.5F + event.getRed()));
        event.setGreen(green = ((0.36F - event.getGreen()) * factor * 0.5F + event.getGreen()));
        event.setBlue(blue = ((0.38F - event.getBlue()) * factor * 0.5F + event.getBlue()));
    }

    private static LocalPlayer player() {
        return Minecraft.getInstance().player;
    }
}
