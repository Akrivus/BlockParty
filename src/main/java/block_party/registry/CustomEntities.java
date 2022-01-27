package block_party.registry;

import block_party.BlockParty;
import block_party.entities.Moe;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CustomEntities {
    public static final RegistryObject<EntityType<Moe>> NPC = BlockParty.ENTITIES.register("npc", () -> EntityType.Builder.of(Moe::new, MobCategory.CREATURE).sized(0.60F, 1.35F).setTrackingRange(32).setUpdateInterval(2).build("npc"));

    public static void add(DeferredRegister<EntityType<?>> registry, IEventBus bus) {
        bus.addListener(CustomEntities::registerAttributes);
        registry.register(bus);
    }

    private static void registerAttributes(EntityAttributeCreationEvent e) {
        e.put(NPC.get(), Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.ATTACK_DAMAGE, 2.0).add(Attributes.MOVEMENT_SPEED, 0.4).add(Attributes.ATTACK_SPEED, 2.0).add(Attributes.FLYING_SPEED, 1.6).add(Attributes.FOLLOW_RANGE, 256.0).build());
    }
}
