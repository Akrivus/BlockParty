package block_party.registry;

import block_party.BlockParty;
import block_party.blocks.AbstractDataBlock;
import block_party.blocks.DataSaplingBlock;
import block_party.blocks.GardenLanternBlock;
import block_party.blocks.GinkgoLeavesBlock;
import block_party.blocks.HangingScrollBlock;
import block_party.blocks.PaperLanternBlock;
import block_party.blocks.SakuraBlossomsBlock;
import block_party.blocks.ShimenawaBlock;
import block_party.blocks.ShojiLanternBlock;
import block_party.blocks.ShojiScreenBlock;
import block_party.blocks.ShrineTabletBlock;
import block_party.blocks.WisteriaLeavesBlock;
import block_party.blocks.WisteriaVineBodyBlock;
import block_party.blocks.WisteriaVineTipBlock;
import block_party.blocks.WritingTableBlock;
import block_party.blocks.entity.GardenLanternBlockEntity;
import block_party.blocks.entity.HangingScrollBlockEntity;
import block_party.blocks.entity.PaperLanternBlockEntity;
import block_party.blocks.entity.SakuraSaplingBlockEntity;
import block_party.blocks.entity.ShimenawaBlockEntity;
import block_party.blocks.entity.ShrineTabletBlockEntity;
import block_party.scene.SceneObservations;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
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
    public static final DeferredBlock<Block> SHOJI_SCREEN = registerShojiScreen("shoji_screen");
    public static final DeferredBlock<Block> WISTERIA_BINE = registerPillar("wisteria_bine");
    public static final DeferredBlock<Block> WISTERIA_LEAVES = registerLeaves("wisteria_leaves");
    public static final DeferredBlock<Block> WISTERIA_PLANKS = register("wisteria_planks");
    public static final DeferredBlock<Block> WISTERIA_VINE_BODY = registerWisteriaVineBody("wisteria_vine_body");
    public static final DeferredBlock<Block> WISTERIA_VINE_TIP = registerWisteriaVineTip("wisteria_vine_tip");
    public static final DeferredBlock<Block> PAPER_LANTERN = registerPaperLantern("black_paper_lantern");
    public static final DeferredBlock<Block> BLANK_HANGING_SCROLL = registerHangingScroll("blank_hanging_scroll", SceneObservations.ALWAYS);
    public static final DeferredBlock<Block> BLUE_PAPER_LANTERN = registerPaperLantern("blue_paper_lantern");
    public static final DeferredBlock<Block> BROWN_PAPER_LANTERN = registerPaperLantern("brown_paper_lantern");
    public static final DeferredBlock<Block> CYAN_PAPER_LANTERN = registerPaperLantern("cyan_paper_lantern");
    public static final DeferredBlock<Block> DAWN_HANGING_SCROLL = registerHangingScroll("dawn_hanging_scroll", SceneObservations.DAWN);
    public static final DeferredBlock<Block> EVENING_HANGING_SCROLL = registerHangingScroll("evening_hanging_scroll", SceneObservations.EVENING);
    public static final DeferredBlock<Block> GARDEN_LANTERN = registerGardenLantern("garden_lantern");
    public static final DeferredBlock<Block> GRAY_PAPER_LANTERN = registerPaperLantern("gray_paper_lantern");
    public static final DeferredBlock<Block> GREEN_PAPER_LANTERN = registerPaperLantern("green_paper_lantern");
    public static final DeferredBlock<Block> LIGHT_BLUE_PAPER_LANTERN = registerPaperLantern("light_blue_paper_lantern");
    public static final DeferredBlock<Block> LIGHT_GRAY_PAPER_LANTERN = registerPaperLantern("light_gray_paper_lantern");
    public static final DeferredBlock<Block> LIME_PAPER_LANTERN = registerPaperLantern("lime_paper_lantern");
    public static final DeferredBlock<Block> MAGENTA_PAPER_LANTERN = registerPaperLantern("magenta_paper_lantern");
    public static final DeferredBlock<Block> MIDNIGHT_HANGING_SCROLL = registerHangingScroll("midnight_hanging_scroll", SceneObservations.MIDNIGHT);
    public static final DeferredBlock<Block> MORNING_HANGING_SCROLL = registerHangingScroll("morning_hanging_scroll", SceneObservations.MORNING);
    public static final DeferredBlock<Block> NIGHT_HANGING_SCROLL = registerHangingScroll("night_hanging_scroll", SceneObservations.NIGHT);
    public static final DeferredBlock<Block> NOON_HANGING_SCROLL = registerHangingScroll("noon_hanging_scroll", SceneObservations.NOON);
    public static final DeferredBlock<Block> ORANGE_PAPER_LANTERN = registerPaperLantern("orange_paper_lantern");
    public static final DeferredBlock<Block> PINK_PAPER_LANTERN = registerPaperLantern("pink_paper_lantern");
    public static final DeferredBlock<Block> PURPLE_PAPER_LANTERN = registerPaperLantern("purple_paper_lantern");
    public static final DeferredBlock<Block> RED_PAPER_LANTERN = registerPaperLantern("red_paper_lantern");
    public static final DeferredBlock<Block> SAKURA_SAPLING = registerDataSapling("sakura_sapling", CustomWorldGen.SAKURA_TREE, SakuraSaplingBlockEntity::new);
    public static final DeferredBlock<Block> SHIMENAWA = registerShimenawa("shimenawa");
    public static final DeferredBlock<Block> SHRINE_TABLET = registerShrineTablet("shrine_tablet");
    public static final DeferredBlock<Block> SHOJI_LANTERN = registerShojiLantern("shoji_lantern");
    public static final DeferredBlock<Block> WHITE_PAPER_LANTERN = registerPaperLantern("white_paper_lantern");
    public static final DeferredBlock<Block> WHITE_SAKURA_SAPLING = registerDataSapling("white_sakura_sapling", CustomWorldGen.WHITE_SAKURA_TREE, SakuraSaplingBlockEntity::new);
    public static final DeferredBlock<Block> WRITING_TABLE = registerWritingTable("writing_table");
    public static final DeferredBlock<Block> YELLOW_PAPER_LANTERN = registerPaperLantern("yellow_paper_lantern");

    static {
        registerSlab("ginkgo_slab");
        registerButton("ginkgo_button", BlockSetType.CHERRY);
        registerButton("sakura_button", BlockSetType.CHERRY);
        registerButton("wisteria_button", BlockSetType.CHERRY);
        registerFence("ginkgo_fence");
        registerFence("sakura_fence");
        registerFence("wisteria_fence");
        registerFenceGate("ginkgo_fence_gate", WoodType.CHERRY);
        registerFenceGate("sakura_fence_gate", WoodType.CHERRY);
        registerFenceGate("wisteria_fence_gate", WoodType.CHERRY);
        registerPressurePlate("ginkgo_pressure_plate", BlockSetType.CHERRY);
        registerPressurePlate("sakura_pressure_plate", BlockSetType.CHERRY);
        registerPressurePlate("wisteria_pressure_plate", BlockSetType.CHERRY);
        registerStairs("ginkgo_stairs", GINKGO_PLANKS);
        registerStairs("sakura_stairs", SAKURA_PLANKS);
        registerSlab("wisteria_slab");
        registerStairs("wisteria_stairs", WISTERIA_PLANKS);
        registerPillar("tatami_mat");
        registerTrapDoor("shoji_panel", BlockSetType.CHERRY);
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
        registerPotted("potted_ginkgo_sapling", "ginkgo_sapling");
        registerPotted("potted_sakura_sapling", "sakura_sapling");
        registerPotted("potted_white_sakura_sapling", "white_sakura_sapling");
        registerPotted("potted_wisteria_sapling", "wisteria_sapling");
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
        DeferredBlock<Block> block = switch (id) {
            case "ginkgo_leaves" -> BLOCKS.registerBlock(id, GinkgoLeavesBlock::new, leavesProperties());
            case "wisteria_leaves" -> BLOCKS.registerBlock(id, WisteriaLeavesBlock::new, leavesProperties());
            default -> BLOCKS.registerBlock(id, net.minecraft.world.level.block.LeavesBlock::new, leavesProperties());
        };
        ENTRIES.put(id, block);
        return block;
    }

    private static BlockBehaviour.Properties leavesProperties() {
        return BlockBehaviour.Properties.of().strength(0.2F).randomTicks().noOcclusion().sound(SoundType.GRASS);
    }

    private static DeferredBlock<Block> registerSakuraBlossoms(String id) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        java.util.function.Supplier<net.minecraft.core.particles.SimpleParticleType> particle = switch (id) {
            case "white_sakura_blossoms" -> CustomParticles.WHITE_SAKURA::get;
            default -> CustomParticles.SAKURA::get;
        };
        DeferredBlock<Block> block = BLOCKS.registerBlock(id,
                properties -> new SakuraBlossomsBlock(particle, properties),
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

    private static DeferredBlock<Block> registerButton(String id, BlockSetType blockSetType) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, properties -> new ButtonBlock(blockSetType, 30, properties),
                BlockBehaviour.Properties.of().strength(0.5F).noCollission().sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerFence(String id) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, FenceBlock::new,
                BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerFenceGate(String id, WoodType woodType) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, properties -> new FenceGateBlock(woodType, properties),
                BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerPressurePlate(String id, BlockSetType blockSetType) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, properties -> new PressurePlateBlock(blockSetType, properties),
                BlockBehaviour.Properties.of().strength(0.5F).noCollission().sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerStairs(String id, DeferredBlock<Block> baseBlock) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, properties -> new StairBlock(baseBlock.get().defaultBlockState(), properties),
                BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerTrapDoor(String id, BlockSetType blockSetType) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, properties -> new TrapDoorBlock(blockSetType, properties),
                BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerShojiScreen(String id) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, ShojiScreenBlock::new,
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

    private static DeferredBlock<Block> registerPaperLantern(String id) {
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, PaperLanternBlock::new,
                BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerHangingScroll(String id, SceneObservations condition) {
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, properties -> new HangingScrollBlock(properties, condition),
                BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerGardenLantern(String id) {
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, GardenLanternBlock::new,
                BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerShimenawa(String id) {
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, ShimenawaBlock::new,
                BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerShrineTablet(String id) {
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, ShrineTabletBlock::new,
                BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerShojiLantern(String id) {
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, ShojiLanternBlock::new,
                BlockBehaviour.Properties.of().strength(1.0F).noOcclusion().sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerWritingTable(String id) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> block = BLOCKS.registerBlock(id, WritingTableBlock::new,
                BlockBehaviour.Properties.of().strength(1.0F).sound(SoundType.WOOD));
        ENTRIES.put(id, block);
        return block;
    }

    private static DeferredBlock<Block> registerPotted(String id, String plantId) {
        if (ENTRIES.containsKey(id)) {
            return ENTRIES.get(id);
        }
        DeferredBlock<Block> plant = ENTRIES.get(plantId);
        DeferredBlock<Block> block = BLOCKS.registerBlock(id,
                properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, plant, properties),
                BlockBehaviour.Properties.of().strength(0.0F).noOcclusion().sound(SoundType.STONE));
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
