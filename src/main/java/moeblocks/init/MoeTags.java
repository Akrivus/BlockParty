package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.automata.state.enums.Dere;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class MoeTags {
    public static final ITag.INamedTag<Block> BABY = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/baby"));
    public static final ITag.INamedTag<Block> FULLSIZED = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/fullsized"));
    public static final ITag.INamedTag<Block> GLOWING = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/glowing"));
    public static final ITag.INamedTag<Block> MALE = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/male"));
    public static final ITag.INamedTag<Block> NEKO = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/neko"));
    public static final ITag.INamedTag<Block> WINGED = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/winged"));

    public static final Map<Dere, ITag.INamedTag<Item>> LOVED_GIFTS = new HashMap<>();
    public static final Map<Dere, ITag.INamedTag<Item>> LIKED_GIFTS = new HashMap<>();
    public static final Map<Dere, ITag.INamedTag<Item>> HATED_GIFTS = new HashMap<>();

    public static class Blocks {
        public static final ITag.INamedTag<Block> CHESTS = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "chests"));
        public static final ITag.INamedTag<Block> DOORS = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "doors"));
        public static final ITag.INamedTag<Block> MINEABLES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "mineables"));
        public static final ITag.INamedTag<Block> MOEABLES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moeables"));
        public static final ITag.INamedTag<Block> SAKURA_LOGS = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "sakura_logs"));
        public static final ITag.INamedTag<Block> WISTERIA_BLOCKS = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "wisteria_blocks"));
    }

    public static class Items {
        public static final ITag.INamedTag<Item> ADMIN = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "admin"));
        public static final ITag.INamedTag<Item> DROPS_CROPS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "drops/crops"));
        public static final ITag.INamedTag<Item> DROPS_LOOT = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "drops/loot"));
        public static final ITag.INamedTag<Item> DROPS_ORES = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "drops/ores"));
        public static final ITag.INamedTag<Item> DROPS_SEEDS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "drops/seeds"));
        public static final ITag.INamedTag<Item> EQUIPPABLES = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "equippables"));
        public static final ITag.INamedTag<Item> OFFHAND = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "offhand"));
        public static final ITag.INamedTag<Item> SAKURA_LOGS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "sakura_logs"));
        public static final ITag.INamedTag<Item> TOOLS_ARCHER = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "tools/archer"));
        public static final ITag.INamedTag<Item> TOOLS_FARMER = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "tools/farmer"));
        public static final ITag.INamedTag<Item> TOOLS_FIGHTER = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "tools/fighter"));
        public static final ITag.INamedTag<Item> TOOLS_MINER = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "tools/miner"));
        public static final ITag.INamedTag<Item> TOOLS_SHEPHERD = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "tools/shepherd"));
        public static final ITag.INamedTag<Item> WEAPONS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "weapons"));
        public static final ITag.INamedTag<Item> WISTERIA_BLOCKS = ItemTags.createOptional(new ResourceLocation(MoeMod.ID, "wisteria_blocks"));
    }
}
