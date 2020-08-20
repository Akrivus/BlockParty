package mod.moeblocks.register;

import mod.moeblocks.MoeMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class TagsMoe {
    public static final ITag.INamedTag<Block> MOEABLES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moeables"));
    public static final ITag.INamedTag<Item> EQUIPPABLES = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "equippables"));
    public static final ITag.INamedTag<Item> RELICS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "relics"));
    public static final ITag.INamedTag<Item> TREASURES = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "treasures"));
    public static final ITag.INamedTag<Item> WEAPONS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "weapons"));
    public static final ITag.INamedTag<Item> WONDERS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "wonders"));
    public static final ITag.INamedTag<Item> GIFTABLES = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "giftables"));
}
