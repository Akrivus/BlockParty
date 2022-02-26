package block_party.items;

import block_party.BlockParty;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class SamuraiKatanaItem extends SwordItem {
    public SamuraiKatanaItem() {
        super(new Tier() {
            @Override
            public int getUses() {
                return 2880;
            }

            @Override
            public float getSpeed() {
                return 10.0F;
            }

            @Override
            public float getAttackDamageBonus() {
                return 5.0F;
            }

            @Override
            public int getLevel() {
                return 4;
            }

            @Override
            public int getEnchantmentValue() {
                return 19;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.EMPTY;
            }
        }, 4, -1.6F, new Properties().tab(BlockParty.CreativeModeTab).stacksTo(1));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }
}
