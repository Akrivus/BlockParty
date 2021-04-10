package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.client.render.DeerRenderer;
import moeblocks.client.render.MoeDieRenderer;
import moeblocks.client.render.MoeRenderer;
import moeblocks.entity.DeerEntity;
import moeblocks.entity.MoeDieEntity;
import moeblocks.entity.MoeEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, MoeMod.ID);

    public static final RegistryObject<EntityType<DeerEntity>> DEER = REGISTRY.register("deer", () -> EntityType.Builder.create(DeerEntity::new, EntityClassification.CREATURE).size(0.60F, 1.35F).setTrackingRange(10).setUpdateInterval(2).build("deer"));
    public static final RegistryObject<EntityType<MoeDieEntity>> MOE_DIE = REGISTRY.register("moe_die", () -> EntityType.Builder.<MoeDieEntity>create(MoeDieEntity::new, EntityClassification.MISC).size(0.375F, 0.375F).setTrackingRange(4).setUpdateInterval(10).setShouldReceiveVelocityUpdates(true).build("moe_die"));
    public static final RegistryObject<EntityType<MoeEntity>> MOE = REGISTRY.register("moe", () -> EntityType.Builder.create(MoeEntity::new, EntityClassification.CREATURE).size(0.60F, 1.35F).setTrackingRange(32).setUpdateInterval(2).build("moe"));

    public static void registerEntityRenderingHandlers() {
        RenderingRegistry.registerEntityRenderingHandler(MoeEntities.DEER.get(), DeerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MoeEntities.MOE_DIE.get(), MoeDieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MoeEntities.MOE.get(), MoeRenderer::new);
    }

    public static void registerAttributes() {
        registerAttribute(DEER.get(), 10.0, 1.0, 0.2, 16.0);
        registerAttribute(MOE.get(), 20.0, 2.0, 0.4, 256.0);
    }

    private static void registerAttribute(EntityType<? extends LivingEntity> type, double health, double damage, double speed, double range) {
        GlobalEntityTypeAttributes.put(type, MobEntity.func_233666_p_().createMutableAttribute(Attributes.MAX_HEALTH, health).createMutableAttribute(Attributes.ATTACK_DAMAGE, damage).createMutableAttribute(Attributes.MOVEMENT_SPEED, speed).createMutableAttribute(Attributes.ATTACK_SPEED, speed * 5).createMutableAttribute(Attributes.FLYING_SPEED, speed * 4).createMutableAttribute(Attributes.FOLLOW_RANGE, range).create());
    }
}
