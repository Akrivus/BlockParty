package moe.blocks.mod.init;

import moe.blocks.mod.MoeMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

public class MoeTags {
    public static final ITag.INamedTag<Block> BABY_MOES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "baby_moes"));
    public static final ITag.INamedTag<Block> FULLSIZED_MOES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "fullsized_moes"));
    public static final ITag.INamedTag<Block> GLOWING_MOES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "glowing_moes"));
    public static final ITag.INamedTag<Block> MALE_MOES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "male_moes"));
    public static final ITag.INamedTag<Block> MINEABLES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "mineables"));
    public static final ITag.INamedTag<Block> MOEABLES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moeables"));
    public static final ITag.INamedTag<Block> OPENABLE_DOORS = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "openable_doors"));
    public static final ITag.INamedTag<Block> WINGED_MOES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "winged_moes"));
    public static final ITag.INamedTag<Item> DANDERE_GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "dandere_gifts"));
    public static final ITag.INamedTag<Item> DEREDERE_GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "deredere_gifts"));
    public static final ITag.INamedTag<Item> EQUIPPABLES = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "equippables"));
    public static final ITag.INamedTag<Item> FARMING_TOOLS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "farming_tools"));
    public static final ITag.INamedTag<Item> GIFTABLES = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "giftables"));
    public static final ITag.INamedTag<Item> HIMEDERE_GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "himedere_gifts"));
    public static final ITag.INamedTag<Item> KUUDERE_GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "kuudere_gifts"));
    public static final ITag.INamedTag<Item> MELEE_WEAPONS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "melee_weapons"));
    public static final ITag.INamedTag<Item> MINING_TOOLS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "mining_tools"));
    public static final ITag.INamedTag<Item> RANGE_WEAPONS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "range_weapons"));
    public static final ITag.INamedTag<Item> TSUNDERE_GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "tsundere_gifts"));
    public static final ITag.INamedTag<Item> YANDERE_GIFTS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "yandere_gifts"));
}
