package moeblocks.item;

import moeblocks.MoeMod;
import net.minecraft.item.Item;

public class BrushItem extends Item {
    public BrushItem() {
        super(new Properties().group(MoeMod.ITEMS).maxStackSize(1).maxDamage(64));
    }
}
