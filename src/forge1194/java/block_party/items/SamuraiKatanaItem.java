package block_party.items;

import block_party.BlockParty;
import block_party.registry.CustomSounds;
import block_party.registry.CustomTags;
import block_party.utils.CustomDamageSource;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class SamuraiKatanaItem extends SwordItem {
    protected SamuraiKatanaItem(Tier tier) {
        super(tier, 4, -1.6F, new Properties().stacksTo(1));
    }

    public SamuraiKatanaItem() {
        this(new Tier() {
            @Override
            public int getUses() {
                return 288;
            }

            @Override
            public float getSpeed() {
                return 15.0F;
            }

            @Override
            public float getAttackDamageBonus() {
                return 3.0F;
            }

            @Override
            public int getLevel() {
                return 2;
            }

            @Override
            public int getEnchantmentValue() {
                return 7;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.EMPTY;
            }
        });
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }

    @SubscribeEvent
    public static void onSwingAttack(LivingAttackEvent e) {
        DamageSource damage = e.getSource();
        if (damage.is(DamageTypeTags.BYPASSES_ARMOR) || damage.is(DamageTypeTags.IS_PROJECTILE))
            return;
        LivingEntity attacker = (LivingEntity) damage.getEntity();
        if (e.getEntity() instanceof ServerPlayer victim && victim.getAttackStrengthScale(1.0F) < 1.0D) {
            e.setCanceled(victim.getMainHandItem().is(CustomTags.Items.PARRY_SWORDS));
            if (e.isCanceled()) {
                attacker.playSound(CustomSounds.KATANA_PARRY.get(), 1.0F, 1.2F / (victim.level.random.nextFloat() * 0.2F + 0.9F));
                double x = victim.getX() - attacker.getX();
                double z = victim.getZ() - attacker.getZ();
                attacker.knockback(0.8F, x, z);
                Registry<DamageType> reg = attacker.level.registryAccess().registry(Registries.DAMAGE_TYPE).get();
                attacker.hurt(new DamageSource(reg.getHolder(DamageTypes.GENERIC).get()), e.getAmount() * 2.0F);
                victim.sweepAttack();
            }
        }
    }
}
