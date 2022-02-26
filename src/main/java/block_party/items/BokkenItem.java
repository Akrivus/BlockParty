package block_party.items;

import block_party.BlockParty;
import net.minecraft.world.item.*;

public class BokkenItem extends SwordItem {
    public BokkenItem() {
        super(Tiers.WOOD, 4, -1.6F, new Properties().tab(BlockParty.CreativeModeTab).stacksTo(1));
    }
}
