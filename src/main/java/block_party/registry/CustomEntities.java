package block_party.registry;

import block_party.BlockParty;
import block_party.entities.Moe;
import block_party.entities.MoeInHiding;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CustomEntities {
    public static final Supplier<EntityType<Moe>> MOE = BlockParty.ENTITIES.register("moe", () -> EntityType.Builder.<Moe>of(Moe::new, MobCategory.CREATURE).sized(0.60F, 1.35F).setTrackingRange(32).setUpdateInterval(2).build("moe"));
    public static final Supplier<EntityType<MoeInHiding>> MOE_IN_HIDING = BlockParty.ENTITIES.register("moe_in_hiding", () -> EntityType.Builder.<MoeInHiding>of(MoeInHiding::new, MobCategory.MISC).sized(1.0F, 0.0F).build("moe_in_hiding"));

    public static void add(DeferredRegister<EntityType<?>> registry, IEventBus bus) {
        bus.addListener(CustomEntities::registerAttributes);
        registry.register(bus);
    }

    private static void registerAttributes(EntityAttributeCreationEvent e) {
        e.put(MOE.get(), Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.MOVEMENT_SPEED, 0.4).add(Attributes.ATTACK_SPEED, 2.0).add(Attributes.FLYING_SPEED, 1.6).add(Attributes.FOLLOW_RANGE, 256.0).build());
    }
}
