package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.client.render.layer.MoeSpecialRenderer;
import moeblocks.client.render.layer.special.BarrelOverlay;
import net.minecraft.block.*;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.HashMap;

public class MoeOverrides {
    protected static HashMap<Block, SoundEvent> STEP_SOUNDS = new HashMap<>();
    protected static HashMap<Block, Property<?>> PROPS = new HashMap<>();
    protected static HashMap<Block, Block> ALIASES = new HashMap<>();
    
    public static ResourceLocation getNameOf(BlockState state) {
        return MoeOverrides.getNameOf(state, null);
    }
    
    public static ResourceLocation getNameOf(BlockState state, String suffix) {
        Block block = state.getBlock();
        String key = MoeOverrides.get(block).getRegistryName().toString().replace(':', '/');
        if (PROPS.containsKey(block)) { key += String.format(".%s", state.get(PROPS.get(block)).toString()); }
        if (block.isIn(MoeTags.Blocks.CHESTS) && MoeMod.isChristmas()) { suffix = "christmas"; }
        if (suffix != null) { key += String.format(".%s", suffix); }
        return new ResourceLocation(MoeMod.ID, String.format("textures/entity/moe/skins/%s.png", key));
    }
    
    public static Block get(Block block) {
        return ALIASES.getOrDefault(block, block);
    }
    
    public static void registerAliases() {
        rename(Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD);
        rename(Blocks.ACACIA_PLANKS, Blocks.ACACIA_FENCE, Blocks.ACACIA_FENCE_GATE, Blocks.ACACIA_SLAB, Blocks.ACACIA_STAIRS);
        rename(Blocks.ANDESITE, Blocks.ANDESITE_SLAB, Blocks.ANDESITE_STAIRS, Blocks.ANDESITE_WALL, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_ANDESITE_SLAB, Blocks.POLISHED_ANDESITE_STAIRS);
        rename(Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD);
        rename(Blocks.BIRCH_PLANKS, Blocks.BIRCH_FENCE, Blocks.BIRCH_FENCE_GATE, Blocks.BIRCH_SLAB, Blocks.BIRCH_STAIRS);
        rename(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE);
        rename(Blocks.BLACKSTONE, Blocks.BLACKSTONE_SLAB, Blocks.BLACKSTONE_STAIRS, Blocks.BLACKSTONE_WALL, Blocks.POLISHED_BLACKSTONE, Blocks.POLISHED_BLACKSTONE_SLAB, Blocks.POLISHED_BLACKSTONE_STAIRS, Blocks.POLISHED_BLACKSTONE_WALL);
        rename(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE);
        rename(Blocks.BRICKS, Blocks.BRICK_SLAB, Blocks.BRICK_STAIRS, Blocks.BRICK_WALL);
        rename(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE);
        rename(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS);
        rename(Blocks.COBBLESTONE, Blocks.COBBLESTONE_SLAB, Blocks.COBBLESTONE_STAIRS, Blocks.COBBLESTONE_WALL, Blocks.INFESTED_COBBLESTONE);
        rename(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
        rename(Blocks.CRIMSON_PLANKS, Blocks.CRIMSON_FENCE, Blocks.CRIMSON_FENCE_GATE, Blocks.CRIMSON_SLAB, Blocks.CRIMSON_STAIRS);
        rename(Blocks.CRIMSON_STEM, Blocks.CRIMSON_HYPHAE);
        rename(Blocks.CUT_SANDSTONE, Blocks.CUT_SANDSTONE_SLAB);
        rename(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE);
        rename(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD);
        rename(Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE, Blocks.DARK_OAK_FENCE_GATE, Blocks.DARK_OAK_SLAB, Blocks.DARK_OAK_STAIRS);
        rename(Blocks.DARK_PRISMARINE, Blocks.DARK_PRISMARINE_SLAB, Blocks.DARK_PRISMARINE_STAIRS);
        rename(Blocks.DIORITE, Blocks.DIORITE_SLAB, Blocks.DIORITE_STAIRS, Blocks.DIORITE_WALL, Blocks.POLISHED_DIORITE, Blocks.POLISHED_DIORITE_SLAB, Blocks.POLISHED_DIORITE_STAIRS);
        rename(Blocks.DIRT, Blocks.COARSE_DIRT);
        rename(Blocks.END_STONE_BRICKS, Blocks.END_STONE_BRICK_SLAB, Blocks.END_STONE_BRICK_STAIRS, Blocks.END_STONE_BRICK_WALL);
        rename(Blocks.GLASS, Blocks.GLASS_PANE);
        rename(Blocks.GRANITE, Blocks.GRANITE_SLAB, Blocks.GRANITE_STAIRS, Blocks.GRANITE_WALL, Blocks.POLISHED_GRANITE, Blocks.POLISHED_GRANITE_SLAB, Blocks.POLISHED_GRANITE_STAIRS);
        rename(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE);
        rename(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE);
        rename(Blocks.IRON_BLOCK, Blocks.IRON_BARS);
        rename(Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD);
        rename(Blocks.JUNGLE_PLANKS, Blocks.JUNGLE_FENCE, Blocks.JUNGLE_FENCE_GATE, Blocks.JUNGLE_SLAB, Blocks.JUNGLE_STAIRS);
        rename(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        rename(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
        rename(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE);
        rename(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE);
        rename(Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.MOSSY_COBBLESTONE_WALL);
        rename(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.MOSSY_STONE_BRICK_WALL);
        rename(Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICK_WALL);
        rename(Blocks.OAK_LOG, Blocks.OAK_WOOD);
        rename(Blocks.OAK_PLANKS, Blocks.OAK_FENCE, Blocks.OAK_FENCE_GATE, Blocks.OAK_SLAB, Blocks.OAK_STAIRS);
        rename(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE);
        rename(Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE);
        rename(Blocks.POLISHED_BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
        rename(Blocks.PRISMARINE, Blocks.PRISMARINE_SLAB, Blocks.PRISMARINE_STAIRS, Blocks.PRISMARINE_WALL);
        rename(Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICK_SLAB, Blocks.PRISMARINE_BRICK_STAIRS);
        rename(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE);
        rename(Blocks.PURPUR_BLOCK, Blocks.PURPUR_SLAB, Blocks.PURPUR_STAIRS);
        rename(Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_SLAB, Blocks.QUARTZ_STAIRS);
        rename(Blocks.RED_NETHER_BRICKS, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICK_WALL);
        rename(Blocks.RED_SANDSTONE, Blocks.RED_SANDSTONE_SLAB, Blocks.RED_SANDSTONE_STAIRS, Blocks.RED_SANDSTONE_WALL);
        rename(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE);
        rename(Blocks.SANDSTONE, Blocks.SANDSTONE_SLAB, Blocks.SANDSTONE_STAIRS, Blocks.SANDSTONE_WALL);
        rename(Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ_STAIRS);
        rename(Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
        rename(Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_SANDSTONE_STAIRS);
        rename(Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
        rename(Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD);
        rename(Blocks.SPRUCE_PLANKS, Blocks.SPRUCE_FENCE, Blocks.SPRUCE_FENCE_GATE, Blocks.SPRUCE_SLAB, Blocks.SPRUCE_STAIRS);
        rename(Blocks.STONE, Blocks.INFESTED_STONE, Blocks.SMOOTH_STONE, Blocks.SMOOTH_STONE_SLAB, Blocks.STONE_SLAB, Blocks.STONE_STAIRS);
        rename(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICK_SLAB, Blocks.STONE_BRICK_STAIRS, Blocks.STONE_BRICK_WALL);
        rename(Blocks.STRIPPED_ACACIA_LOG, Blocks.STRIPPED_ACACIA_WOOD);
        rename(Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_BIRCH_WOOD);
        rename(Blocks.STRIPPED_CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_HYPHAE);
        rename(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_WOOD);
        rename(Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_WOOD);
        rename(Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_OAK_WOOD);
        rename(Blocks.STRIPPED_SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_WOOD);
        rename(Blocks.STRIPPED_WARPED_STEM, Blocks.STRIPPED_WARPED_HYPHAE);
        rename(Blocks.WARPED_PLANKS, Blocks.WARPED_FENCE, Blocks.WARPED_FENCE_GATE, Blocks.WARPED_SLAB, Blocks.WARPED_STAIRS);
        rename(Blocks.WARPED_STEM, Blocks.WARPED_HYPHAE);
        rename(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE);
        rename(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE);
    }
    
    public static void rename(Block main, Block... aliases) {
        for (Block alias : aliases) {
            ALIASES.put(alias, main);
        }
    }
    
    public static void registerPropertyOverrides() {
        registerProperty(Blocks.CAKE, CakeBlock.BITES);
        registerProperty(Blocks.NOTE_BLOCK, NoteBlock.NOTE);
    }
    
    private static void registerProperty(Block block, Property<?> property) {
        PROPS.put(block, property);
    }
    
    public static void registerStepSounds() {
        registerStepSound(Blocks.BELL, MoeSounds.ENTITY_MOE_BELL_STEP.get());
    }
    
    private static void registerStepSound(Block block, SoundEvent sound) {
        STEP_SOUNDS.put(block, sound);
    }
    
    public static SoundEvent getStepSound(BlockState block) {
        return STEP_SOUNDS.getOrDefault(block.getBlock(), block.getSoundType().getStepSound());
    }

    public static void registerSpecialRenderers() {
        MoeSpecialRenderer.registerOverlay(Blocks.BARREL, BarrelOverlay::new);
    }
}
