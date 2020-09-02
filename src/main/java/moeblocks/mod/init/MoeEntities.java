package moeblocks.mod.init;

import moeblocks.mod.MoeMod;
import moeblocks.mod.client.render.MoeDieRenderer;
import moeblocks.mod.client.render.MoeRenderer;
import moeblocks.mod.client.render.SenpaiRenderer;
import moeblocks.mod.entity.MoeDieEntity;
import moeblocks.mod.entity.MoeEntity;
import moeblocks.mod.entity.SenpaiEntity;
import moeblocks.mod.entity.StudentEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, MoeMod.ID);

    public static final RegistryObject<EntityType<MoeDieEntity>> MOE_DIE = REGISTRY.register("moe_die", () -> EntityType.Builder.<MoeDieEntity>create(MoeDieEntity::new, EntityClassification.MISC).size(0.375F, 0.375F).setTrackingRange(4).setUpdateInterval(10).setShouldReceiveVelocityUpdates(true).build("moe_die"));
    public static final RegistryObject<EntityType<MoeEntity>> MOE = REGISTRY.register("moe", () -> EntityType.Builder.create(MoeEntity::new, EntityClassification.CREATURE).size(0.70F, 1.35F).setTrackingRange(32).setUpdateInterval(2).build("moe"));
    public static final RegistryObject<EntityType<SenpaiEntity>> SENPAI = REGISTRY.register("senpai", () -> EntityType.Builder.create(SenpaiEntity::new, EntityClassification.CREATURE).size(0.60F, 1.80F).setTrackingRange(32).setUpdateInterval(2).build("senpai"));

    public static void registerEntityRenderingHandlers() {
        RenderingRegistry.registerEntityRenderingHandler(MoeEntities.MOE_DIE.get(), MoeDieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MoeEntities.MOE.get(), MoeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MoeEntities.SENPAI.get(), SenpaiRenderer::new);
    }

    public static void registerAttributes() {
        GlobalEntityTypeAttributes.put(SENPAI.get(), StudentEntity.setCustomAttributes());
        GlobalEntityTypeAttributes.put(MOE.get(), StudentEntity.setCustomAttributes());
    }
}
