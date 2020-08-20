package mod.moeblocks.entity.util;

import mod.moeblocks.entity.ai.behavior.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.function.Supplier;

public enum Behaviors {
    BEACON(BeaconBehavior::new, Blocks.BEACON),
    BEE_NEST(BeeNestBehavior::new, Blocks.BEE_NEST),
    BEEHIVE(BeehiveBehavior::new, Blocks.BEEHIVE),
    BELL(BellBehavior::new, Blocks.BELL),
    BREWING_STAND(BrewingStandBehavior::new, Blocks.BREWING_STAND),
    BRICKS(BricksBehavior::new, Blocks.BRICKS),
    CACTUS(CactusBehavior::new, Blocks.CACTUS),
    CAKE(CakeBehavior::new, Blocks.CAKE),
    CARPET(CarpetBehavior::new, Blocks.BLACK_CARPET, Blocks.BLUE_CARPET, Blocks.BROWN_CARPET, Blocks.CYAN_CARPET, Blocks.GRAY_CARPET, Blocks.GREEN_CARPET, Blocks.LIGHT_BLUE_CARPET, Blocks.LIGHT_GRAY_CARPET, Blocks.LIME_CARPET, Blocks.MAGENTA_CARPET, Blocks.ORANGE_CARPET, Blocks.PINK_CARPET, Blocks.PURPLE_CARPET, Blocks.RED_CARPET, Blocks.WHITE_CARPET, Blocks.YELLOW_CARPET),
    COARSE_DIRT(CoarseDirtBehavior::new, Blocks.COARSE_DIRT),
    CONDUIT(ConduitBehavior::new, Blocks.CONDUIT),
    DEFAULT(BasicBehavior::new),
    DIRT(DirtBehavior::new, Blocks.DIRT),
    DRAGON_EGG(DragonEggBehavior::new, Blocks.DRAGON_EGG),
    DRY_SPONGE(DrySpongeBehavior::new, Blocks.SPONGE),
    FARMLAND(FarmlandBehavior::new, Blocks.FARMLAND),
    FURNACE(FurnaceBehavior::new, Blocks.FURNACE),
    GLAZED_TERRACOTTA(GlazedTerracottaBehavior::new, Blocks.BLACK_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA),
    GLOWSTONE(GlowstoneBehavior::new, Blocks.GLOWSTONE),
    GRASS_BLOCK(GrassBlockBehavior::new, Blocks.GRASS_BLOCK),
    HONEYCOMB_BLOCK(HoneycombBlockBehavior::new, Blocks.HONEYCOMB_BLOCK),
    LOG(LogBehavior::new, Blocks.ACACIA_LOG, Blocks.ACACIA_WOOD, Blocks.BIRCH_LOG, Blocks.BIRCH_WOOD, Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_WOOD, Blocks.JUNGLE_LOG, Blocks.JUNGLE_WOOD, Blocks.OAK_LOG, Blocks.OAK_WOOD, Blocks.SPRUCE_LOG, Blocks.SPRUCE_WOOD),
    MAGMA_BLOCK(MagmaBlockBehavior::new, Blocks.MAGMA_BLOCK),
    MISSING(BasicBehavior::new),
    MYCELIUM(MyceliumBehavior::new, Blocks.MYCELIUM),
    NETHER_BRICKS(NetherBricksBehavior::new, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_SLAB, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_BRICK_WALL, Blocks.NETHER_BRICKS, Blocks.RED_NETHER_BRICK_SLAB, Blocks.RED_NETHER_BRICK_STAIRS, Blocks.RED_NETHER_BRICK_WALL, Blocks.RED_NETHER_BRICKS),
    NOTE_BLOCK(NoteBlockBehavior::new, Blocks.NOTE_BLOCK),
    OBSERVER(ObserverBehavior::new, Blocks.OBSERVER),
    REDSTONE_BLOCK(RedstoneBlockBehavior::new, Blocks.REDSTONE_BLOCK),
    REDSTONE_LAMP(RedstoneLampBehavior::new, Blocks.REDSTONE_LAMP),
    SEA_LANTERN(SeaLanternBehavior::new, Blocks.SEA_LANTERN),
    WET_SPONGE(WetSpongeBehavior::new, Blocks.WET_SPONGE),
    WOOL(WoolBehavior::new, Blocks.BLACK_WOOL, Blocks.BLUE_WOOL, Blocks.BROWN_WOOL, Blocks.CYAN_WOOL, Blocks.GRAY_WOOL, Blocks.GREEN_WOOL, Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_GRAY_WOOL, Blocks.LIME_WOOL, Blocks.MAGENTA_WOOL, Blocks.ORANGE_WOOL, Blocks.PINK_WOOL, Blocks.PURPLE_WOOL, Blocks.RED_WOOL, Blocks.WHITE_WOOL, Blocks.YELLOW_WOOL);

    private final Supplier<? extends AbstractBehavior> behavior;

    Behaviors(final Supplier<? extends AbstractBehavior> behavior, Block... blocks) {
        this.behavior = behavior;
        for (Block block : blocks) {
            Registry.BLOCKS_TO_BEHAVIORS.put(block, this);
        }
    }

    public static Behaviors from(BlockState state) {
        return Registry.BLOCKS_TO_BEHAVIORS.getOrDefault(state.getBlock(), DEFAULT);
    }

    public AbstractBehavior get() {
        return this.behavior.get();
    }

    protected static class Registry {
        public static HashMap<Block, Behaviors> BLOCKS_TO_BEHAVIORS = new HashMap<>();
    }
}
