package block_party.registry;

import block_party.BlockParty;
import block_party.blocks.AbstractDataBlock;
import block_party.blocks.DataSaplingBlock;
import block_party.blocks.SakuraBlossomsBlock;
import block_party.blocks.WisteriaVineBodyBlock;
import block_party.blocks.WisteriaVineTipBlock;
import block_party.blocks.entity.GardenLanternBlockEntity;
import block_party.blocks.entity.HangingScrollBlockEntity;
import block_party.blocks.entity.PaperLanternBlockEntity;
import block_party.blocks.entity.SakuraSaplingBlockEntity;
import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.blocks.entity.ShrineTabletBlockEntity;
import block_party.blocks.entity.WindChimesBlockEntity;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BlockParty.ID);
    public static final Map<String, DeferredBlock<Block>> ENTRIES = new LinkedHashMap<>();

    public static final DeferredBlock<Block> SHOJI_BLOCK = register("shoji_block");
    public static final DeferredBlock<Block> GINKGO_LEAVES = registerLeaves("ginkgo_leaves");
    public static final DeferredBlock<Block> GINKGO_PLANKS = register("ginkgo_planks");
    public static final DeferredBlock<Block> SAKURA_PLANKS = register("sakura_planks");
    public static final DeferredBlock<Block> SAKURA_BLOSSOMS = registerSakuraBlossoms("sakura_blossoms");
    public static final DeferredBlock<Block> WHITE_SAKURA_BLOSSOMS = registerSakuraBlossoms("white_sakura_blossoms");
    public static final DeferredBlock<Block> SAKURA_SLAB = registerSlab("sakura_slab");
    public static final DeferredBlock<Block> SHOJI_SCREEN = registerDoor("shoji_screen");
    public static final DeferredBlock<Block> WISTERIA_BINE = registerPillar("wisteria_bine");
    public static final DeferredBlock<Block> WISTERIA_LEAVES = registerLeaves("wisteria_leaves");
    public static final DeferredBlock<Block> WISTERIA_VINE_BODY = registerWisteriaVineBody("wisteria_vine_body");
    public static final DeferredBlock<Block> WISTERIA_VINE_TIP = registerWisteriaVineTip("wisteria_vine_tip");
    public static final DeferredBlock<Block> PAPER_LANTERN = registerData("black_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> BLANK_HANGING_SCROLL = registerData("blank_hanging_scroll", HangingScrollBlockEntity::new);
    public static final DeferredBlock<Block> BLUE_PAPER_LANTERN = registerData("blue_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> BROWN_PAPER_LANTERN = registerData("brown_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> CYAN_PAPER_LANTERN = registerData("cyan_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> DAWN_HANGING_SCROLL = registerData("dawn_hanging_scroll", HangingScrollBlockEntity::new);
    public static final DeferredBlock<Block> EVENING_HANGING_SCROLL = registerData("evening_hanging_scroll", HangingScrollBlockEntity::new);
    public static final DeferredBlock<Block> GARDEN_LANTERN = registerData("garden_lantern", GardenLanternBlockEntity::new);
    public static final DeferredBlock<Block> GRAY_PAPER_LANTERN = registerData("gray_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> GREEN_PAPER_LANTERN = registerData("green_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> LIGHT_BLUE_PAPER_LANTERN = registerData("light_blue_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> LIGHT_GRAY_PAPER_LANTERN = registerData("light_gray_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> LIME_PAPER_LANTERN = registerData("lime_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> MAGENTA_PAPER_LANTERN = registerData("magenta_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> MIDNIGHT_HANGING_SCROLL = registerData("midnight_hanging_scroll", HangingScrollBlockEntity::new);
    public static final DeferredBlock<Block> MORNING_HANGING_SCROLL = registerData("morning_hanging_scroll", HangingScrollBlockEntity::new);
    public static final DeferredBlock<Block> NIGHT_HANGING_SCROLL = registerData("night_hanging_scroll", HangingScrollBlockEntity::new);
    public static final DeferredBlock<Block> NOON_HANGING_SCROLL = registerData("noon_hanging_scroll", HangingScrollBlockEntity::new);
    public static final DeferredBlock<Block> ORANGE_PAPER_LANTERN = registerData("orange_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> PINK_PAPER_LANTERN = registerData("pink_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> PURPLE_PAPER_LANTERN = registerData("purple_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> RED_PAPER_LANTERN = registerData("red_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> SAKURA_SAPLING = registerDataSapling("sakura_sapling", CustomWorldGen.SAKURA_TREE, SakuraSaplingBlockEntity::new);
    public static final DeferredBlock<Block> SHIMENAWA = registerData("shimenawa", ShimenawaBlockEntity::new);
    public static final DeferredBlock<Block> SHRINE_TABLET = registerData("shrine_tablet", ShrineTabletBlockEntity::new);
    public static final DeferredBlock<Block> WHITE_PAPER_LANTERN = registerData("white_paper_lantern", PaperLanternBlockEntity::new);
    public static final DeferredBlock<Block> WHITE_SAKURA_SAPLING = registerDataSapling("white_sakura_sapling", CustomWorldGen.WHITE_SAKURA_TREE, SakuraSaplingBlockEntity::new);
    public static final DeferredBlock<Block> WIND_CHIMES = registerData("wind_chimes", WindChimesBlockEntity::new);
    public static final DeferredBlock<Block> YELLOW_PAPER_LANTERN = registerData("yellow_paper_lantern", PaperLanternBlockEntity::new);

    static {
        registerRemaining(
                "ginkgo_button",
                "ginkgo_fence",
                "ginkgo_fence_gate",
                "ginkgo_pressure_plate",
                "ginkgo_slab",
                "ginkgo_stairs",
                "sakura_button",
                "sakura_fence",
                "sakura_fence_gate",
                "sakura_pressure_plate",
                "sakura_stairs",
                "shoji_lantern",
                "shoji_panel",
                "tatami_mat",
                "writing_table",
                "potted_ginkgo_sapling",
                "potted_sakura_sapling",
                "potted_white_sakura_sapling",
                "potted_wisteria_sapling");
        registerPillar("ginkgo_log");
        registerPillar("ginkgo_wood");
        registerPillar("sakura_log");
        registerPillar("sakura_wood");
        registerPillar("stripped_ginkgo_log");
        registerPillar("stripped_ginkgo_wood");
        registerPillar("stripped_sakura_log");
        registerPillar("stripped_sakura_wood");
        registerSapling("ginkgo_sapling", CustomWorldGen.GINKGO_TREE);
        registerSapling("wisteria_sapling", CustomWorldGen.WISTERIA_TREE);
    }

    private CustomBlocks() {
    }

    private static DeferredBlock<Block> register(String id) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerSimpleBlock(id, BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerPillar(String id) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, RotatedPillarBlock::new,
                BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerLeaves(String id) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, LeavesBlock::new,
                BlockBehaviour.Properties.of().strength(0.2F).randomTicks().noOcclusion().sound(SoundType.GRASS));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerSakuraBlossoms(String id) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, SakuraBlossomsBlock::new,
                BlockBehaviour.Properties.of().strength(0.2F).randomTicks().noOcclusion().sound(SoundType.GRASS));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerSapling(String id, ResourceKey<ConfiguredFeature<?, ?>> feature) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        TreeGrower grower = new TreeGrower(id, java.util.Optional.empty(), java.util.Optional.of(feature), java.util.Optional.empty());
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, properties -> new SaplingBlock(grower, properties),
                BlockBehaviour.Properties.of().strength(0.0F).randomTicks().noCollission().sound(SoundType.GRASS));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerSlab(String id) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, SlabBlock::new,
                BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerDoor(String id) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, properties -> new DoorBlock(BlockSetType.CHERRY, properties),
                BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerDataSapling(String id, ResourceKey<ConfiguredFeature<?, ?>> feature, BiFunction<BlockPos, BlockState, ? extends block_party.blocks.entity.AbstractDataBlockEntity> initializer) {
        TreeGrower grower = new TreeGrower(id, java.util.Optional.empty(), java.util.Optional.of(feature), java.util.Optional.empty());
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, properties -> new DataSaplingBlock(grower, initializer, properties),
                BlockBehaviour.Properties.of().strength(0.0F).randomTicks().noCollission().sound(SoundType.GRASS));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerWisteriaVineBody(String id) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, WisteriaVineBodyBlock::new,
                BlockBehaviour.Properties.of().strength(0.2F).noCollission().noOcclusion().sound(SoundType.GRASS));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerWisteriaVineTip(String id) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, WisteriaVineTipBlock::new,
                BlockBehaviour.Properties.of().strength(0.2F).noCollission().noOcclusion().sound(SoundType.GRASS));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerData(String id, BiFunction<BlockPos, BlockState, ? extends block_party.blocks.entity.AbstractDataBlockEntity> initializer) {
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, properties -> new AbstractDataBlock(initializer, properties),
                BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD));
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
