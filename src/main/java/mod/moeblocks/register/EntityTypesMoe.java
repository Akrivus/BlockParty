package mod.moeblocks.register;

import mod.moeblocks.MoeMod;
import mod.moeblocks.client.render.MoeDieRenderer;
import mod.moeblocks.client.render.MoeRenderer;
import mod.moeblocks.entity.MoeDieEntity;
import mod.moeblocks.entity.MoeEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypesMoe {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, MoeMod.ID);

    public static final RegistryObject<EntityType<MoeDieEntity>> MOE_DIE = REGISTRY.register("moe_die", () -> EntityType.Builder.<MoeDieEntity>create(MoeDieEntity::new, EntityClassification.MISC).size(0.375F, 0.375F).setTrackingRange(4).setUpdateInterval(10).setShouldReceiveVelocityUpdates(true).build("moe_die"));
    public static final RegistryObject<EntityType<MoeEntity>> MOE = REGISTRY.register("moe", () -> EntityType.Builder.create(MoeEntity::new, EntityClassification.CREATURE).size(0.70F, 1.35F).setTrackingRange(32).setUpdateInterval(20).setShouldReceiveVelocityUpdates(true).build("moe"));

    public static void registerEntityRenderingHandlers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesMoe.MOE_DIE.get(), MoeDieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTypesMoe.MOE.get(), MoeRenderer::new);
    }
}
