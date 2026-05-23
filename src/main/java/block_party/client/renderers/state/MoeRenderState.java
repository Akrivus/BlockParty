package block_party.client.renderers.state;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class MoeRenderState extends HumanoidRenderState {
    public ResourceLocation texture;
    public ResourceLocation eyeTexture;
    public ResourceLocation faceTexture;
    public ResourceLocation glowTexture;
    public BlockState visibleBlockState;
    public boolean hasWings;
    public boolean hasCatFeatures;
    public boolean hasGlow;
    public boolean onGround;
    public boolean isSitting;
    public String animation = "DEFAULT";
    public float moeScale = 1.0F;
    public float slouch;
    public float health;
    public float maxHealth;
    public final float[] eyeColor = new float[] { 1.0F, 1.0F, 1.0F };
}
