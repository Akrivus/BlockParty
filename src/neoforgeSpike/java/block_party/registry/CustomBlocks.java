package block_party.registry;

import block_party.BlockParty;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BlockParty.ID);
    public static final Map<String, DeferredBlock<Block>> ENTRIES = new LinkedHashMap<>();

    public static final DeferredBlock<Block> SHOJI_BLOCK = register("shoji_block");
    public static final DeferredBlock<Block> GINKGO_LEAVES = register("ginkgo_leaves");
    public static final DeferredBlock<Block> GINKGO_PLANKS = register("ginkgo_planks");
    public static final DeferredBlock<Block> SAKURA_PLANKS = register("sakura_planks");
    public static final DeferredBlock<Block> PAPER_LANTERN = register("black_paper_lantern");

    static {
        registerRemaining(
                "blank_hanging_scroll",
                "blue_paper_lantern",
                "brown_paper_lantern",
                "cyan_paper_lantern",
                "dawn_hanging_scroll",
                "evening_hanging_scroll",
                "garden_lantern",
                "ginkgo_button",
                "ginkgo_fence",
                "ginkgo_fence_gate",
                "ginkgo_log",
                "ginkgo_pressure_plate",
                "ginkgo_sapling",
                "ginkgo_slab",
                "ginkgo_stairs",
                "ginkgo_wood",
                "gray_paper_lantern",
                "green_paper_lantern",
                "light_blue_paper_lantern",
                "light_gray_paper_lantern",
                "lime_paper_lantern",
                "magenta_paper_lantern",
                "midnight_hanging_scroll",
                "morning_hanging_scroll",
                "night_hanging_scroll",
                "noon_hanging_scroll",
                "orange_paper_lantern",
                "pink_paper_lantern",
                "purple_paper_lantern",
                "red_paper_lantern",
                "sakura_blossoms",
                "sakura_button",
                "sakura_fence",
                "sakura_fence_gate",
                "sakura_log",
                "sakura_pressure_plate",
                "sakura_sapling",
                "sakura_slab",
                "sakura_stairs",
                "sakura_wood",
                "shimenawa",
                "shoji_lantern",
                "shoji_panel",
                "shoji_screen",
                "stripped_ginkgo_log",
                "stripped_ginkgo_wood",
                "stripped_sakura_log",
                "stripped_sakura_wood",
                "tatami_mat",
                "shrine_tablet",
                "white_paper_lantern",
                "white_sakura_blossoms",
                "white_sakura_sapling",
                "wind_chimes",
                "wisteria_bine",
                "wisteria_leaves",
                "wisteria_sapling",
                "wisteria_vine_body",
                "wisteria_vine_tip",
                "writing_table",
                "yellow_paper_lantern",
                "potted_ginkgo_sapling",
                "potted_sakura_sapling",
                "potted_white_sakura_sapling",
                "potted_wisteria_sapling");
    }

    private CustomBlocks() {
    }

    private static DeferredBlock<Block> register(String id) {
        DeferredBlock<Block> block = BLOCKS.registerSimpleBlock(id, BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static void registerRemaining(String... ids) {
        for (String id : ids) {
            if (!ENTRIES.containsKey(id)) {
                register(id);
            }
        }
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }
}
