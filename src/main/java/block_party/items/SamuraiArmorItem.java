package block_party.items;

import block_party.registry.CustomTags;
import java.util.EnumMap;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class SamuraiArmorItem extends ArmorItem implements SortableItem {
    public static final ArmorMaterial MATERIAL = new ArmorMaterial(
            12,
            defense(),
            7,
            SoundEvents.ARMOR_EQUIP_CHAIN,
            1.0F,
            0.2F,
            ItemTags.REPAIRS_IRON_ARMOR,
            EquipmentAssets.CHAINMAIL);

    public SamuraiArmorItem(Properties properties, ArmorType type) {
        this(properties, type, Rarity.RARE);
    }

    protected SamuraiArmorItem(Properties properties, ArmorType type, Rarity rarity) {
        super(MATERIAL, type, properties.rarity(rarity));
    }

    public static int repairWithExperience(ItemStack stack, int xpValue) {
        if (xpValue <= 0 || !stack.is(CustomTags.Items.SAMURAI_ITEMS) || !stack.isDamaged()) {
            return xpValue;
        }
        int repair = Math.min(xpValue, stack.getDamageValue());
        stack.setDamageValue(stack.getDamageValue() - repair);
        return xpValue - repair;
    }

    public static double reduceArrowDamageForSamuraiItems(LivingEntity target, double baseDamage) {
        double damage = baseDamage;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (target.getItemBySlot(slot).is(CustomTags.Items.SAMURAI_ITEMS)) {
                damage *= 0.5D;
            }
        }
        return damage;
    }

    public static void onXpPickup(PlayerXpEvent.PickupXp event) {
        ExperienceOrb orb = event.getOrb();
        if (event.getEntity() instanceof ServerPlayer player) {
            EquipmentSlot slot = EquipmentSlot.values()[orb.level().random.nextInt(5)];
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.is(CustomTags.Items.SAMURAI_ITEMS)) {
                int leftover = repairWithExperience(stack, orb.getValue());
                if (leftover > 0) {
                    player.giveExperiencePoints(leftover);
                }
                orb.discard();
                event.setCanceled(true);
            }
        }
    }

    public static void onArrowImpact(ProjectileImpactEvent event) {
        HitResult result = event.getRayTraceResult();
        if (result.getType() != HitResult.Type.ENTITY) {
            return;
        }
        EntityHitResult entityHit = (EntityHitResult) result;
        if (event.getProjectile() instanceof AbstractArrow arrow && entityHit.getEntity() instanceof LivingEntity target) {
            arrow.setBaseDamage(reduceArrowDamageForSamuraiItems(target, arrow.getBaseDamage()));
        }
    }

    @Override
    public int getSortOrder() {
        return 10;
    }

    private static EnumMap<ArmorType, Integer> defense() {
        EnumMap<ArmorType, Integer> defense = new EnumMap<>(ArmorType.class);
        defense.put(ArmorType.HELMET, 3);
        defense.put(ArmorType.CHESTPLATE, 4);
        defense.put(ArmorType.LEGGINGS, 5);
        defense.put(ArmorType.BOOTS, 2);
        return defense;
    }
}
