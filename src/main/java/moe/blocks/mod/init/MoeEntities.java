package moe.blocks.mod.init;

import moe.blocks.mod.MoeMod;
import moe.blocks.mod.client.render.MoeDieRenderer;
import moe.blocks.mod.client.render.MoeRenderer;
import moe.blocks.mod.client.render.SenpaiRenderer;
import moe.blocks.mod.entity.MoeDieEntity;
import moe.blocks.mod.entity.MoeEntity;
import moe.blocks.mod.entity.SenpaiEntity;
import moe.blocks.mod.entity.FiniteEntity;
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
        GlobalEntityTypeAttributes.put(SENPAI.get(), FiniteEntity.setCustomAttributes());
        GlobalEntityTypeAttributes.put(MOE.get(), FiniteEntity.setCustomAttributes());
    }
}
