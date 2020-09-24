package moe.blocks.mod.init;

import moe.blocks.mod.MoeMod;
import moe.blocks.mod.entity.ai.automata.state.Deres;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class MoeTags {
    public static final ITag.INamedTag<Block> BABY_MOES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "baby_moes"));
    public static final ITag.INamedTag<Block> FULLSIZED_MOES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "fullsized_moes"));
    public static final ITag.INamedTag<Block> GLOWING_MOES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "glowing_moes"));
    public static final ITag.INamedTag<Block> MALE_MOES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "male_moes"));
    public static final ITag.INamedTag<Block> MINEABLES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "mineables"));
    public static final ITag.INamedTag<Block> MOEABLES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moeables"));
    public static final ITag.INamedTag<Block> OPENABLE_DOORS = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "openable_doors"));
    public static final ITag.INamedTag<Block> WINGED_MOES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "winged_moes"));
    public static final ITag.INamedTag<Item> BREEDING_TOOLS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "breeding_tools"));
    public static final ITag.INamedTag<Item> EQUIPPABLES = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "equippables"));
    public static final ITag.INamedTag<Item> FARMING_TOOLS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "farming_tools"));
    public static final ITag.INamedTag<Item> OFFHAND_ITEMS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "offhand_items"));
    public static final ITag.INamedTag<Item> MELEE_WEAPONS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "melee_weapons"));
    public static final ITag.INamedTag<Item> MINING_TOOLS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "mining_tools"));
    public static final ITag.INamedTag<Item> RANGED_WEAPONS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "ranged_weapons"));

    public static final Map<Deres, ITag.INamedTag<Item>> LOVED_GIFTS = new HashMap<>();
    public static final Map<Deres, ITag.INamedTag<Item>> LIKED_GIFTS = new HashMap<>();
    public static final Map<Deres, ITag.INamedTag<Item>> HATED_GIFTS = new HashMap<>();
}
