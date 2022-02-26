package block_party.registry;

import block_party.BlockParty;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CustomTags {
    public static final Tag.Named<Block> HAS_CAT_FEATURES = BlockTags.createOptional(BlockParty.source("moe/has_cat_features"));
    public static final Tag.Named<Block> HAS_FESTIVE_TEXTURES = BlockTags.createOptional(BlockParty.source("moe/has_festive_textures"));
    public static final Tag.Named<Block> HAS_GLOW = BlockTags.createOptional(BlockParty.source("moe/has_glow"));
    public static final Tag.Named<Block> HAS_MALE_PRONOUNS = BlockTags.createOptional(BlockParty.source("moe/has_male_pronouns"));
    public static final Tag.Named<Block> HAS_NONBINARY_PRONOUNS = BlockTags.createOptional(BlockParty.source("moe/has_nonbinary_pronouns"));
    public static final Tag.Named<Block> HAS_WINGS = BlockTags.createOptional(BlockParty.source("moe/has_wings"));
    public static final Tag.Named<Block> IGNORES_VOLUME = BlockTags.createOptional(BlockParty.source("moe/ignores_volume"));

    public static class Blocks {
        public static final Tag.Named<Block> GINKGO_LOGS = BlockTags.createOptional(BlockParty.source("ginkgo_logs"));
        public static final Tag.Named<Block> GINKGO_WOOD = BlockTags.createOptional(BlockParty.source("ginkgo_wood"));
        public static final Tag.Named<Block> SAKURA_LOGS = BlockTags.createOptional(BlockParty.source("sakura_logs"));
        public static final Tag.Named<Block> SAKURA_WOOD = BlockTags.createOptional(BlockParty.source("sakura_wood"));
        public static final Tag.Named<Block> SHRINE_BASE_BLOCKS = BlockTags.createOptional(BlockParty.source("shrine_base_blocks"));
        public static final Tag.Named<Block> SPAWNS_MOES = BlockTags.createOptional(BlockParty.source("spawns_moes"));
        public static final Tag.Named<Block> SPAWNS_FIREFLIES = BlockTags.createOptional(BlockParty.source("spawns_fireflies"));
        public static final Tag.Named<Block> WISTERIA = BlockTags.createOptional(BlockParty.source("wisteria"));
    }

    public static class Items {
        public static final Tag.Named<Item> GINKGO_LOGS = ItemTags.createOptional(BlockParty.source("ginkgo_logs"));
        public static final Tag.Named<Item> GINKGO_WOOD = ItemTags.createOptional(BlockParty.source("ginkgo_wood"));
        public static final Tag.Named<Item> SAKURA_LOGS = ItemTags.createOptional(BlockParty.source("sakura_logs"));
        public static final Tag.Named<Item> SAKURA_WOOD = ItemTags.createOptional(BlockParty.source("sakura_wood"));
        public static final Tag.Named<Item> SAMURAI_ITEMS = ItemTags.createOptional(BlockParty.source("samurai_items"));
        public static final Tag.Named<Item> WISTERIA = ItemTags.createOptional(BlockParty.source("wisteria"));
    }
}
