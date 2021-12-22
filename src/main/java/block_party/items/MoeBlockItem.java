package block_party.items;

import block_party.BlockParty;
import block_party.utils.sorters.ISortableItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

public class MoeBlockItem extends BlockItem implements ISortableItem {
    protected final int sortOrder;

    public MoeBlockItem(RegistryObject<Block> block) {
        this(block, 100);
    }

    public MoeBlockItem(RegistryObject<Block> block, int sortOrder) {
        super(block.get(), new Item.Properties().tab(BlockParty.CreativeModeTab));
        this.sortOrder = sortOrder;
    }

    @Override
    public int getSortOrder() {
        return this.sortOrder;
    }
}
