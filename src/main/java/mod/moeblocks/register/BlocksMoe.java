package mod.moeblocks.register;

import mod.moeblocks.MoeMod;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlocksMoe {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MoeMod.ID);

    public static class Tags {
        public static final Tag<Block> MOEABLES = new BlockTags.Wrapper(new ResourceLocation(MoeMod.ID, "moeables"));
    }
}
