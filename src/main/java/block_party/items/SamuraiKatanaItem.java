package block_party.items;

import block_party.registry.CustomSounds;
import block_party.registry.CustomTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class SamuraiKatanaItem extends SwordItem implements SortableItem {
    public static final ToolMaterial MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL,
            288,
            15.0F,
            3.0F,
            7,
            CustomTags.Items.NO_REPAIR);

    public SamuraiKatanaItem(Properties properties) {
        this(MATERIAL, properties.rarity(Rarity.RARE));
    }

    protected SamuraiKatanaItem(ToolMaterial material, Properties properties) {
        super(material, 4.0F, -1.6F, properties.stacksTo(1));
    }

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (source.is(DamageTypeTags.BYPASSES_ARMOR) || source.is(DamageTypeTags.IS_PROJECTILE)) {
            return;
        }
        if (event.getEntity() instanceof ServerPlayer victim
                && source.getEntity() instanceof LivingEntity attacker
                && tryParry(victim, attacker, event.getAmount())) {
            event.setCanceled(true);
        }
    }

    public static boolean tryParry(ServerPlayer victim, LivingEntity attacker, float amount) {
        if (victim.getAttackStrengthScale(1.0F) >= 1.0F || !victim.getMainHandItem().is(CustomTags.Items.PARRY_SWORDS)) {
            return false;
        }
        attacker.playSound(CustomSounds.ENTRIES.get("item.katana.parry").get(), 1.0F, 1.0F);
        attacker.knockback(0.8F, victim.getX() - attacker.getX(), victim.getZ() - attacker.getZ());
        if (attacker.level() instanceof ServerLevel serverLevel) {
            attacker.hurtServer(serverLevel, attacker.damageSources().generic(), amount * 2.0F);
        }
        victim.sweepAttack();
        return true;
    }

    @Override
    public int getSortOrder() {
        return 10;
    }
}
