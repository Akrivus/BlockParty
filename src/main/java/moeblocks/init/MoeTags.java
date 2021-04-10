package moeblocks.init;

import moeblocks.MoeMod;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class MoeTags {
    public static final ITag.INamedTag<Block> BABY = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/baby"));
    public static final ITag.INamedTag<Block> FESTIVES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/festive"));
    public static final ITag.INamedTag<Block> FULLSIZED = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/fullsized"));
    public static final ITag.INamedTag<Block> GLOWING = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/glowing"));
    public static final ITag.INamedTag<Block> MALE = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/male"));
    public static final ITag.INamedTag<Block> NEKO = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/neko"));
    public static final ITag.INamedTag<Block> WINGED = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moes/winged"));

    public static class Blocks {
        public static final ITag.INamedTag<Block> MOEABLES = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "moeables"));
        public static final ITag.INamedTag<Block> SAKURA_LOGS = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "sakura_logs"));
        public static final ITag.INamedTag<Block> SAKURA_WOOD = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "sakura_wood"));
        public static final ITag.INamedTag<Block> WISTERIA = BlockTags.createOptional(new ResourceLocation(MoeMod.ID, "wisteria"));
    }

    public static class Items {

    }
}
