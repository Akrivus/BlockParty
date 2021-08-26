package block_party.init;

import block_party.BlockParty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;

public class BlockPartyTags {
    public static final Tag.Named<Block> BABY = BlockTags.createOptional(new ResourceLocation(BlockParty.ID, "partyers/baby"));
    public static final Tag.Named<Block> FESTIVES = BlockTags.createOptional(new ResourceLocation(BlockParty.ID, "partyers/festive"));
    public static final Tag.Named<Block> FULLSIZED = BlockTags.createOptional(new ResourceLocation(BlockParty.ID, "partyers/fullsized"));
    public static final Tag.Named<Block> GLOWING = BlockTags.createOptional(new ResourceLocation(BlockParty.ID, "partyers/glowing"));
    public static final Tag.Named<Block> MALE = BlockTags.createOptional(new ResourceLocation(BlockParty.ID, "partyers/male"));
    public static final Tag.Named<Block> NEKO = BlockTags.createOptional(new ResourceLocation(BlockParty.ID, "partyers/neko"));
    public static final Tag.Named<Block> WINGED = BlockTags.createOptional(new ResourceLocation(BlockParty.ID, "partyers/winged"));

    public static class Blocks {
        public static final Tag.Named<Block> FIREFLY_BLOCKS = BlockTags.createOptional(new ResourceLocation(BlockParty.ID, "firefly_blocks"));
        public static final Tag.Named<Block> PARTYER_BLOCKS = BlockTags.createOptional(new ResourceLocation(BlockParty.ID, "partyer_blocks"));
        public static final Tag.Named<Block> SAKURA_LOGS = BlockTags.createOptional(new ResourceLocation(BlockParty.ID, "sakura_logs"));
        public static final Tag.Named<Block> SAKURA_WOOD = BlockTags.createOptional(new ResourceLocation(BlockParty.ID, "sakura_wood"));
        public static final Tag.Named<Block> WISTERIA = BlockTags.createOptional(new ResourceLocation(BlockParty.ID, "wisteria"));
    }

    public static class Items {

    }
}
