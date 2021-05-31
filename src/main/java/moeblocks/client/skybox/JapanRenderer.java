package moeblocks.client.skybox;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import moeblocks.MoeMod;
import moeblocks.init.MoeParticles;
import moeblocks.init.MoeTags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class JapanRenderer {
    private static final ResourceLocation FUJI_TEXTURE = new ResourceLocation(MoeMod.ID, "textures/fuji.png");
    private static final int size = 288;
    private static int horizon = 0;
    private static float r = 0.0F;
    private static float g = 0.0F;
    private static float b = 0.0F;
    private static final float a = 0.5F;

    @SubscribeEvent
    public static void setFogData(EntityViewRenderEvent.FogColors e) {
        horizon = Minecraft.getInstance().gameSettings.renderDistanceChunks * 16;
        r = e.getRed();
        g = e.getGreen();
        b = e.getBlue();
    }

    @SubscribeEvent
    public static void renderFuji(RenderWorldLastEvent e) {
        MatrixStack stack = e.getMatrixStack();
        Minecraft minecraft = Minecraft.getInstance();
        ClientPlayerEntity player = minecraft.player;
        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(r, g, b, a);
        stack.push();
        stack.rotate(Vector3f.XP.rotationDegrees(-90.0F));
        stack.rotate(Vector3f.YP.rotationDegrees(180.0F));
        stack.translate(0, 0, -158 + (player.getPosY() / 32));
        minecraft.textureManager.bindTexture(FUJI_TEXTURE);
        Matrix4f matrix = stack.getLast().getMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(matrix, -size / 2, horizon, -size / 2).tex(0.0F, 0.0F).endVertex();
        buffer.pos(matrix, size / 2, horizon, -size / 2).tex(1.0F, 0.0F).endVertex();
        buffer.pos(matrix, size / 2, horizon, size / 2).tex(1.0F, 1.0F).endVertex();
        buffer.pos(matrix, -size / 2, horizon, size / 2).tex(0.0F, 1.0F).endVertex();
        tessellator.draw();
        stack.pop();
    }

    @SubscribeEvent
    public static void renderFireflies(TickEvent.PlayerTickEvent e) {
        World world = e.player.world;
        if (world instanceof ServerWorld) { return; }
        long time = world.getDayTime() % 24000;
        if (21500 < time || time < 12500) { return; }
        for (int generation = 0; generation < 4; ++generation) {
            float x = world.rand.nextInt(128) - 64;
            float z = world.rand.nextInt(128) - 64;
            for (int y = 0; y > e.player.getPosY() - 255; --y) {
                BlockPos pos = e.player.getPosition().add(x, y, z);
                if (world.getBlockState(pos).isIn(MoeTags.Blocks.FIREFLY_BLOCKS)) {
                    world.addParticle(MoeParticles.FIREFLY.get(), pos.getX(), pos.getY(), pos.getZ(), x, y, z);
                    break;
                }
            }
        }
    }
}
