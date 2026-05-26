package block_party.registry;

import block_party.BlockParty;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, BlockParty.ID);
    public static final Map<String, DeferredHolder<EntityType<?>, ? extends EntityType<?>>> ENTRIES = new LinkedHashMap<>();

    public static final DeferredHolder<EntityType<?>, EntityType<Moe>> MOE = ENTITIES.register("moe", () -> EntityType.Builder
            .of(Moe::new, MobCategory.CREATURE)
            .sized(0.6F, 1.35F)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, BlockParty.source("moe"))));
    public static final DeferredHolder<EntityType<?>, EntityType<MoeInHiding>> MOE_IN_HIDING = ENTITIES.register("moe_in_hiding", () -> EntityType.Builder
            .of(MoeInHiding::new, MobCategory.MISC)
            .sized(1.0F, 0.1F)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, BlockParty.source("moe_in_hiding"))));

    static {
        ENTRIES.put("moe", MOE);
        ENTRIES.put("moe_in_hiding", MOE_IN_HIDING);
    }

    private CustomEntities() {
    }

    public static void register(IEventBus modBus) {
        ENTITIES.register(modBus);
        modBus.addListener(CustomEntities::registerAttributes);
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(MOE.get(), Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_SPEED, 2.0D)
                .add(Attributes.FLYING_SPEED, 1.6D)
                .add(Attributes.FOLLOW_RANGE, 256.0D)
                .build());
    }
}
