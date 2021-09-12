package block_party.init;

import block_party.BlockParty;
import block_party.mob.BlockPartyNPC;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = BlockParty.ID)
public class BlockPartyEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, BlockParty.ID);

    public static final RegistryObject<EntityType<BlockPartyNPC>> NPC = REGISTRY.register("npc", () -> EntityType.Builder.of(BlockPartyNPC::new, MobCategory.CREATURE).sized(0.60F, 1.35F).setTrackingRange(32).setUpdateInterval(2).build("npc"));

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent e) {
        e.put(NPC.get(), Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_SPEED, 2.0)
                .add(Attributes.FLYING_SPEED, 1.6)
                .add(Attributes.FOLLOW_RANGE, 256.0)
                .build());
    }
}
