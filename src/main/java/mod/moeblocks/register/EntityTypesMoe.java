package mod.moeblocks.register;

import mod.moeblocks.MoeMod;
import mod.moeblocks.client.render.MoeDieRenderer;
import mod.moeblocks.client.render.MoeRenderer;
import mod.moeblocks.client.render.SenpaiRenderer;
import mod.moeblocks.entity.MoeDieEntity;
import mod.moeblocks.entity.MoeEntity;
import mod.moeblocks.entity.SenpaiEntity;
import mod.moeblocks.entity.StateEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypesMoe {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, MoeMod.ID);

    public static final RegistryObject<EntityType<MoeDieEntity>> MOE_DIE = REGISTRY.register("moe_die", () -> EntityType.Builder.<MoeDieEntity>create(MoeDieEntity::new, EntityClassification.MISC).size(0.375F, 0.375F).setTrackingRange(4).setUpdateInterval(10).setShouldReceiveVelocityUpdates(true).build("moe_die"));
    public static final RegistryObject<EntityType<MoeEntity>> MOE = REGISTRY.register("moe", () -> EntityType.Builder.create(MoeEntity::new, EntityClassification.CREATURE).size(0.70F, 1.35F).setTrackingRange(32).setUpdateInterval(2).build("moe"));
    public static final RegistryObject<EntityType<SenpaiEntity>> SENPAI = REGISTRY.register("senpai", () -> EntityType.Builder.create(SenpaiEntity::new, EntityClassification.CREATURE).size(0.60F, 1.80F).setTrackingRange(32).setUpdateInterval(2).build("senpai"));

    public static void registerEntityRenderingHandlers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesMoe.MOE_DIE.get(), MoeDieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesMoe.MOE.get(), MoeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesMoe.SENPAI.get(), SenpaiRenderer::new);
    }

    public static void registerAttributes() {
        GlobalEntityTypeAttributes.put(SENPAI.get(), StateEntity.setCustomAttributes());
        GlobalEntityTypeAttributes.put(MOE.get(), StateEntity.setCustomAttributes());
    }
}
