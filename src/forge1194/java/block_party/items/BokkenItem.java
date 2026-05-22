package block_party.items;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class BokkenItem extends SamuraiKatanaItem {
    public BokkenItem() {
        super(new Tier() {
            @Override
            public int getUses() {
                return 88;
            }

            @Override
            public float getSpeed() {
                return 15.0F;
            }

            @Override
            public float getAttackDamageBonus() {
                return 0.0F;
            }

            @Override
            public int getLevel() {
                return 1;
            }

            @Override
            public int getEnchantmentValue() {
                return 2;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.of(ItemTags.PLANKS);
            }
        });
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }
}
