package block_party.items;

import block_party.BlockParty;
import block_party.utils.sorters.ISortableItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class MoeBlockItem extends BlockItem implements ISortableItem {
    protected final int sortOrder;

    public MoeBlockItem(Supplier<Block> block) {
        this(block, 100);
    }

    public MoeBlockItem(Supplier<Block> block, int sortOrder) {
        super(block.get(), new Item.Properties());
        this.sortOrder = sortOrder;
    }

    @Override
    public int getSortOrder() {
        return this.sortOrder;
    }
}
