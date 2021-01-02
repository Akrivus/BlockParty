package moeblocks.item;

import moeblocks.MoeMod;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

public class MoeBlockItem extends BlockItem {
    public MoeBlockItem(RegistryObject<Block> block) {
        super(block.get(), new Item.Properties().group(MoeMod.ITEMS));
    }
}
