package moeblocks.item;

import moeblocks.MoeMod;
import moeblocks.util.sort.ISortableItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;

public class MoeBlockItem extends BlockItem implements ISortableItem {
    protected final int sortOrder;

    public MoeBlockItem(RegistryObject<Block> block) {
        this(block, 100);
    }

    public MoeBlockItem(RegistryObject<Block> block, int sortOrder) {
        super(block.get(), new Item.Properties().group(MoeMod.ITEMS));
        this.sortOrder = sortOrder;
    }

    @Override
    public int getSortOrder() {
        return this.sortOrder;
    }
}
