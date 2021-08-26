package block_party.items;

import block_party.BlockParty;
import block_party.util.sort.ISortableItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fmllegacy.RegistryObject;

public class MoeBlockItem extends BlockItem implements ISortableItem {
    protected final int sortOrder;

    public MoeBlockItem(RegistryObject<Block> block) {
        this(block, 100);
    }

    public MoeBlockItem(RegistryObject<Block> block, int sortOrder) {
        super(block.get(), new Item.Properties().tab(BlockParty.ITEMS));
        this.sortOrder = sortOrder;
    }

    @Override
    public int getSortOrder() {
        return this.sortOrder;
    }
}
