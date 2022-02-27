package block_party.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class MaskedSamuraiItem extends SamuraiArmorItem {
    public MaskedSamuraiItem(EquipmentSlot slot) {
        super("samurai_mask", slot);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.EPIC;
    }
}
