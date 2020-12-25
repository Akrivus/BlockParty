package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.client.render.MoeDieRenderer;
import moeblocks.client.render.MoeRenderer;
import moeblocks.entity.MoeDieEntity;
import moeblocks.entity.MoeEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, MoeMod.ID);
    
    public static final RegistryObject<EntityType<MoeDieEntity>> MOE_DIE = REGISTRY.register("moe_die", () -> EntityType.Builder.<MoeDieEntity>create(MoeDieEntity::new, EntityClassification.MISC).size(0.375F, 0.375F).setTrackingRange(4).setUpdateInterval(10).setShouldReceiveVelocityUpdates(true).build("moe_die"));
    public static final RegistryObject<EntityType<MoeEntity>> MOE = REGISTRY.register("moe", () -> EntityType.Builder.create(MoeEntity::new, EntityClassification.CREATURE).size(0.60F, 1.35F).setTrackingRange(32).setUpdateInterval(2).build("moe"));
    
    public static void registerEntityRenderingHandlers() {
        RenderingRegistry.registerEntityRenderingHandler(MoeEntities.MOE_DIE.get(), MoeDieRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MoeEntities.MOE.get(), MoeRenderer::new);
    }
    
    public static void registerAttributes() {
        GlobalEntityTypeAttributes.put(MOE.get(), MoeEntities.getMap().createMutableAttribute(Attributes.MAX_HEALTH, 20.0F).createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.4F).create());
    }
    
    private static AttributeModifierMap.MutableAttribute getMap() {
        return MobEntity.func_233666_p_().createMutableAttribute(Attributes.ATTACK_DAMAGE, 2.0F).createMutableAttribute(Attributes.ATTACK_SPEED, 4.0F).createMutableAttribute(Attributes.FOLLOW_RANGE, 256.0D).createMutableAttribute(Attributes.FLYING_SPEED, 1.5F);
    }
}
