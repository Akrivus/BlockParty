package block_party.items;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;

public class BokkenItem extends SamuraiKatanaItem {
    public static final ToolMaterial MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL,
            88,
            15.0F,
            0.0F,
            2,
            ItemTags.PLANKS);

    public BokkenItem(Properties properties) {
        super(MATERIAL, properties.rarity(Rarity.UNCOMMON));
    }
}
