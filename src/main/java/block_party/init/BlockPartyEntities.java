package block_party.init;

import block_party.BlockParty;
import block_party.client.render.MoeRenderer;
import block_party.mob.Partyer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockPartyEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, BlockParty.ID);

    public static final RegistryObject<EntityType<Partyer>> PARTYER = REGISTRY.register("partyer", () -> EntityType.Builder.of(Partyer::new, MobCategory.CREATURE).sized(0.60F, 1.35F).setTrackingRange(32).setUpdateInterval(2).build("partyer"));

    public static void registerEntityRenderingHandlers(EntityRenderersEvent.RegisterRenderers e) {
        e.registerEntityRenderer(PARTYER.get(), MoeRenderer::new);
    }

    public static void registerAttributes() {
        registerAttribute(PARTYER.get(), 20.0, 2.0, 0.4, 256.0);
    }

    private static void registerAttribute(EntityType<? extends LivingEntity> type, double health, double damage, double speed, double range) {
        DefaultAttributes.put(type, Mob.createMobAttributes().add(Attributes.MAX_HEALTH, health).add(Attributes.ATTACK_DAMAGE, damage).add(Attributes.MOVEMENT_SPEED, speed).add(Attributes.ATTACK_SPEED, speed * 5).add(Attributes.FLYING_SPEED, speed * 4).add(Attributes.FOLLOW_RANGE, range).build());
    }
}
