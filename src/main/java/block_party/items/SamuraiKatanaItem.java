package block_party.items;

import block_party.registry.CustomSounds;
import block_party.registry.CustomTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;
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

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
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

    public static boolean tryParry(Player victim, LivingEntity attacker, float amount) {
        if (!canParry(victim)) {
            return false;
        }
        attacker.playSound(CustomSounds.ENTRIES.get("item.katana.parry").get(), 1.0F, 1.2F / (victim.level().random.nextFloat() * 0.2F + 0.9F));
        attacker.knockback(0.8F, victim.getX() - attacker.getX(), victim.getZ() - attacker.getZ());
        if (attacker.level() instanceof ServerLevel serverLevel) {
            attacker.hurtServer(serverLevel, attacker.damageSources().generic(), amount * 2.0F);
        }
        victim.sweepAttack();
        return true;
    }

    public static boolean canParry(Player victim) {
        return victim.isUsingItem() && victim.getUseItem().is(CustomTags.Items.PARRY_SWORDS);
    }

    @Override
    public int getSortOrder() {
        return 10;
    }
}
