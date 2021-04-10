package moeblocks.client.render;

import moeblocks.MoeMod;
import moeblocks.client.model.DeerModel;
import moeblocks.entity.DeerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class DeerRenderer extends MobRenderer<DeerEntity, DeerModel<DeerEntity>> implements IRenderFactory<DeerEntity> {
    private final ResourceLocation DEER_TEXTURE = new ResourceLocation(MoeMod.ID, "textures/entity/deer.png");

    public DeerRenderer(EntityRendererManager manager) {
        super(manager, new DeerModel<>(), 0.7F);
    }

    @Override
    public MobRenderer<DeerEntity, DeerModel<DeerEntity>> createRenderFor(EntityRendererManager manager) {
        return new DeerRenderer(manager);
    }

    @Override
    public ResourceLocation getEntityTexture(DeerEntity entity) {
        return DEER_TEXTURE;
    }
}
