package block_party.registry;

import block_party.BlockParty;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public class CustomTags {
    public static final Tag.Named<Block> HAS_CAT_FEATURES = BlockTags.createOptional(BlockParty.source("doll/has_cat_features"));
    public static final Tag.Named<Block> HAS_FESTIVE_TEXTURES = BlockTags.createOptional(BlockParty.source("doll/has_festive_textures"));
    public static final Tag.Named<Block> HAS_GLOW = BlockTags.createOptional(BlockParty.source("doll/has_glow"));
    public static final Tag.Named<Block> HAS_MALE_PRONOUNS = BlockTags.createOptional(BlockParty.source("doll/has_male_pronouns"));
    public static final Tag.Named<Block> HAS_NONBINARY_PRONOUNS = BlockTags.createOptional(BlockParty.source("doll/has_nonbinary_pronouns"));
    public static final Tag.Named<Block> HAS_WINGS = BlockTags.createOptional(BlockParty.source("doll/has_wings"));

    public static class Blocks {
        public static final Tag.Named<Block> SAKURA_LOGS = BlockTags.createOptional(BlockParty.source("sakura_logs"));
        public static final Tag.Named<Block> SAKURA_WOOD = BlockTags.createOptional(BlockParty.source("sakura_wood"));
        public static final Tag.Named<Block> SHRINE_BASE_BLOCKS = BlockTags.createOptional(BlockParty.source("shrine_base_blocks"));
        public static final Tag.Named<Block> SPAWNS_DOLLS = BlockTags.createOptional(BlockParty.source("spawns_dolls"));
        public static final Tag.Named<Block> SPAWNS_FIREFLIES = BlockTags.createOptional(BlockParty.source("spawns_fireflies"));
        public static final Tag.Named<Block> WISTERIA = BlockTags.createOptional(BlockParty.source("wisteria"));
    }

    public static class Items {

    }
}
