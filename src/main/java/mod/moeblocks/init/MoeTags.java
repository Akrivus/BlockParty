package mod.moeblocks.init;

import mod.moeblocks.MoeMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class MoeTags {
    public static final ITag.INamedTag<Block> MOEABLES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moeables"));
    public static final ITag.INamedTag<Item> EQUIPPABLES = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "equippables"));
    public static final ITag.INamedTag<Item> DANDERE_GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "gifts_dandere"));
    public static final ITag.INamedTag<Item> DEREDERE_GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "gifts_deredere"));
    public static final ITag.INamedTag<Item> HIMEDERE_GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "gifts_himedere"));
    public static final ITag.INamedTag<Item> KUUDERE_GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "gifts_kuudere"));
    public static final ITag.INamedTag<Item> TSUNDERE_GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "gifts_tsundere"));
    public static final ITag.INamedTag<Item> YANDERE_GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "gifts_yandere"));
    public static final ITag.INamedTag<Item> GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "gifts"));
    public static final ITag.INamedTag<Item> WEAPONS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "weapons"));
}
