package block_party.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.Mod;

@Mod.EventBusSubscriber
public class MaskedSamuraiItem extends SamuraiArmorItem {
    public MaskedSamuraiItem(EquipmentSlot slot, Type type) {
        super("samurai_mask", slot, type);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }
}
