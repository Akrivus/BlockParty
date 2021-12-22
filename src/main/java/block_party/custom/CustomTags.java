package block_party.custom;

import block_party.BlockParty;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public class CustomTags {
    public static final Tag.Named<Block> BABY = BlockTags.createOptional(BlockParty.source("npcs/baby"));
    public static final Tag.Named<Block> FESTIVES = BlockTags.createOptional(BlockParty.source("npcs/festive"));
    public static final Tag.Named<Block> FULLSIZED = BlockTags.createOptional(BlockParty.source("npcs/fullsized"));
    public static final Tag.Named<Block> GLOWING = BlockTags.createOptional(BlockParty.source("npcs/glowing"));
    public static final Tag.Named<Block> MALE = BlockTags.createOptional(BlockParty.source("npcs/male"));
    public static final Tag.Named<Block> NEKO = BlockTags.createOptional(BlockParty.source("npcs/neko"));
    public static final Tag.Named<Block> WINGED = BlockTags.createOptional(BlockParty.source("npcs/winged"));

    public static class Blocks {
        public static final Tag.Named<Block> FIREFLY_BLOCKS = BlockTags.createOptional(BlockParty.source("firefly_blocks"));
        public static final Tag.Named<Block> NPC_SPAWN_BLOCKS = BlockTags.createOptional(BlockParty.source("npc_spawn_blocks"));
        public static final Tag.Named<Block> SAKURA_LOGS = BlockTags.createOptional(BlockParty.source("sakura_logs"));
        public static final Tag.Named<Block> SAKURA_WOOD = BlockTags.createOptional(BlockParty.source("sakura_wood"));
        public static final Tag.Named<Block> SHRINE_BASE_BLOCKS = BlockTags.createOptional(BlockParty.source("shrine_base_blocks"));
        public static final Tag.Named<Block> WISTERIA = BlockTags.createOptional(BlockParty.source("wisteria"));
    }

    public static class Items {

    }
}
