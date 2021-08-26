package block_party.client.skybox;

import block_party.BlockParty;
import block_party.init.BlockPartyParticles;
import block_party.init.BlockPartyTags;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class JapanRenderer {
    private static final ResourceLocation FUJI_TEXTURE = new ResourceLocation(BlockParty.ID, "textures/fuji.png");
    private static final int size = 288;
    private static int horizon = 0;
    private static float r = 0.0F;
    private static float g = 0.0F;
    private static float b = 0.0F;
    
    @SubscribeEvent
    public static void setFogData(EntityViewRenderEvent.FogColors e) {
        horizon = Minecraft.getInstance().options.renderDistance * 16;
        r = e.getRed();
        g = e.getGreen();
        b = e.getBlue();
    }

    @SubscribeEvent
    public static void renderFuji(RenderWorldLastEvent e) {
        PoseStack stack = e.getMatrixStack();
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(r, g, b, 0.8F);
        stack.pushPose();
        stack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
        stack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        stack.translate(0, 0, -158 + (player.getY() / 32));
        minecraft.textureManager.bind(FUJI_TEXTURE);
        Matrix4f matrix = stack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(7, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(matrix, -size / 2, horizon, -size / 2).uv(0.0F, 0.0F).endVertex();
        buffer.vertex(matrix, size / 2, horizon, -size / 2).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(matrix, size / 2, horizon, size / 2).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(matrix, -size / 2, horizon, size / 2).uv(0.0F, 1.0F).endVertex();
        tessellator.end();
        stack.popPose();
    }

    @SubscribeEvent
    public static void renderFireflies(TickEvent.PlayerTickEvent e) {
        Level world = e.player.level;
        if (world instanceof ServerLevel) { return; }
        long time = world.getDayTime() % 24000;
        if (21500 < time || time < 12500) { return; }
        for (int generation = 0; generation < 4; ++generation) {
            float x = world.random.nextInt(128) - 64;
            float z = world.random.nextInt(128) - 64;
            for (int y = 0; y > e.player.getY() - 255; --y) {
                BlockPos pos = e.player.blockPosition().offset(x, y, z);
                if (world.getBlockState(pos).is(BlockPartyTags.Blocks.FIREFLY_BLOCKS)) {
                    world.addParticle(BlockPartyParticles.FIREFLY.get(), pos.getX(), pos.getY(), pos.getZ(), x, y, z);
                    break;
                }
            }
        }
    }
}
