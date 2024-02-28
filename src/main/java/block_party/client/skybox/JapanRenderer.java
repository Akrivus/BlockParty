package block_party.client.skybox;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.registry.CustomParticles;
import block_party.registry.CustomTags;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Optional;

@Mod.EventBusSubscriber
public class JapanRenderer {
    private static final ResourceLocation FUJI_TEXTURE = BlockParty.source("textures/misc/fuji.png");
    private static final int size = 288;
    private static int horizon = 0;
    private static float distance = Float.POSITIVE_INFINITY;
    private static float bearing = 0.0F;
    private static float r = 0.0F;
    private static float g = 0.0F;
    private static float b = 0.0F;
    private static float factor = 0.0F;

    @SubscribeEvent
    public static void fuji(RenderLevelStageEvent e) {
        PoseStack stack = e.getPoseStack();
        //RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        stack.pushPose();
        stack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        stack.mulPose(Axis.YP.rotationDegrees(180.0F));
        stack.mulPose(Axis.ZP.rotationDegrees(bearing - 90.0F));
        stack.translate(0, 0, -156);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(r, g, b, factor);
        RenderSystem.setShaderTexture(0, FUJI_TEXTURE);
        Matrix4f matrix = stack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, -size / 2, horizon, -size / 2).uv(0.0F, 0.0F).endVertex();
        buffer.vertex(matrix, size / 2, horizon, -size / 2).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, size / 2, horizon, size / 2).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(matrix, -size / 2, horizon, size / 2).uv(0.0F, 1.0F).endVertex();
        tesselator.end();
        stack.popPose();
    }

    @SubscribeEvent
    public static void fireflies(TickEvent.PlayerTickEvent e) {
        Level level = e.player.level;
        if (level instanceof ServerLevel || factor < 0.8F) { return; }
        long time = level.getDayTime() % 24000;
        if (21500 < time || time < 12500) { return; }
        for (int generation = 0; generation < 3; ++generation) {
            float x = level.random.nextInt(128) - 64;
            float z = level.random.nextInt(128) - 64;
            for (int y = 0; y > e.player.getY() - 255; --y) {
                BlockPos pos = e.player.blockPosition().offset((int) x, y, (int) z);
                Vector3f vec = e.player.position().add(new Vec3(x, y, z)).toVector3f();
                if (level.getBlockState(pos).is(CustomTags.Blocks.SPAWNS_FIREFLIES)) {
                    level.addParticle(CustomParticles.FIREFLY.get(), vec.x, pos.getY(), vec.z, bearing, distance, factor);
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void fogData(ViewportEvent.ComputeFogColor e) {
        horizon = Minecraft.getInstance().options.renderDistance().get() * 16;
        Optional<BlockPos> shrine = BlockPartyDB.ShrineLocation.get(player().blockPosition());
        if (shrine.isPresent()) {
            BlockPos pos = shrine.get();
            float x1 = player().getBlockX();
            float x2 = pos.getX();
            float x = x1 - x2;
            float z1 = player().getBlockZ();
            float z2 = pos.getZ();
            float z = z1 - z2;
            bearing = (float) (Math.atan2(z, x) * 180 / Math.PI);
            distance = Math.abs(x) + Math.abs(z);
            factor = (float) (-Math.sqrt(distance / 2048.0 - 0.03125) + 1.0);
            if (distance < 64) { factor = 1.0F; }
            if (distance > 2048) { factor = 0; }
        } else {
            distance = Float.POSITIVE_INFINITY;
            factor = 0;
        }
        e.setRed(r = ((0.95F - e.getRed()) * factor * 0.5F + e.getRed()));
        e.setGreen(g = ((0.36F - e.getGreen()) * factor * 0.5F + e.getGreen()));
        e.setBlue(b = ((0.38F - e.getBlue()) * factor * 0.5F + e.getBlue()));
    }

    private static LocalPlayer player() {
        return Minecraft.getInstance().player;
    }
}
