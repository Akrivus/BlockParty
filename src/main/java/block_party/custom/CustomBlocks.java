package block_party.custom;

import block_party.BlockParty;
import block_party.blocks.*;
import block_party.blocks.tree.GinkgoTree;
import block_party.blocks.tree.SakuraTree;
import block_party.blocks.tree.WhiteSakuraTree;
import block_party.blocks.tree.WisteriaTree;
import block_party.npc.automata.Condition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class CustomBlocks {
    public static final RegistryObject<Block> BLANK_HANGING_SCROLL = BlockParty.BLOCKS.register("blank_hanging_scroll", () -> new HangingScrollBlock(Prop.SHOJI.get(), Condition.NEVER));
    public static final RegistryObject<Block> BLACK_PAPER_LANTERN = BlockParty.BLOCKS.register("black_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.COLOR_BLACK));
    public static final RegistryObject<Block> DAWN_HANGING_SCROLL = BlockParty.BLOCKS.register("dawn_hanging_scroll", () -> new HangingScrollBlock(Prop.SHOJI.get(), Condition.DAWN));
    public static final RegistryObject<Block> EVENING_HANGING_SCROLL = BlockParty.BLOCKS.register("evening_hanging_scroll", () -> new HangingScrollBlock(Prop.SHOJI.get(), Condition.EVENING));
    public static final RegistryObject<Block> MIDNIGHT_HANGING_SCROLL = BlockParty.BLOCKS.register("midnight_hanging_scroll", () -> new HangingScrollBlock(Prop.SHOJI.get(), Condition.MIDNIGHT));
    public static final RegistryObject<Block> MORNING_HANGING_SCROLL = BlockParty.BLOCKS.register("morning_hanging_scroll", () -> new HangingScrollBlock(Prop.SHOJI.get(), Condition.MORNING));
    public static final RegistryObject<Block> NIGHT_HANGING_SCROLL = BlockParty.BLOCKS.register("night_hanging_scroll", () -> new HangingScrollBlock(Prop.SHOJI.get(), Condition.NIGHT));
    public static final RegistryObject<Block> NOON_HANGING_SCROLL = BlockParty.BLOCKS.register("noon_hanging_scroll", () -> new HangingScrollBlock(Prop.SHOJI.get(), Condition.NOON));
    public static final RegistryObject<Block> BLUE_PAPER_LANTERN = BlockParty.BLOCKS.register("blue_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_BLUE));
    public static final RegistryObject<Block> BROWN_PAPER_LANTERN = BlockParty.BLOCKS.register("brown_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_BROWN));
    public static final RegistryObject<Block> CYAN_PAPER_LANTERN = BlockParty.BLOCKS.register("cyan_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_CYAN));
    public static final RegistryObject<Block> GARDEN_LANTERN = BlockParty.BLOCKS.register("garden_lantern", () -> new GardenLanternBlock(Prop.STONE.get()));
    public static final RegistryObject<Block> GRAY_PAPER_LANTERN = BlockParty.BLOCKS.register("gray_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_GRAY));
    public static final RegistryObject<Block> GINKGO_BUTTON = BlockParty.BLOCKS.register("ginkgo_button", () -> new WoodButtonBlock(Prop.NONSOLID.get()));
    public static final RegistryObject<Block> GINKGO_FENCE = BlockParty.BLOCKS.register("ginkgo_fence", () -> new FenceBlock(Prop.NONSOLID.get()));
    public static final RegistryObject<Block> GINKGO_FENCE_GATE = BlockParty.BLOCKS.register("ginkgo_fence_gate", () -> new FenceGateBlock(Prop.NONSOLID.get()));
    public static final RegistryObject<Block> GINKGO_LEAVES = BlockParty.BLOCKS.register("ginkgo_leaves", () -> new GinkgoLeavesBlock(CustomParticles.GINKGO, Prop.PLANT.get()));
    public static final RegistryObject<Block> GINKGO_LOG = BlockParty.BLOCKS.register("ginkgo_log", () -> new RotatedPillarBlock(Prop.SOLID.get()));
    public static final RegistryObject<Block> GINKGO_PLANKS = BlockParty.BLOCKS.register("ginkgo_planks", () -> new Block(Prop.SOLID.get()));
    public static final RegistryObject<Block> GINKGO_PRESSURE_PLATE = BlockParty.BLOCKS.register("ginkgo_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Prop.NONSOLID.get()));
    public static final RegistryObject<Block> GINKGO_SAPLING = BlockParty.BLOCKS.register("ginkgo_sapling", () -> new SaplingBlock(new GinkgoTree(), Prop.PLANT.get()));
    public static final RegistryObject<Block> GINKGO_SLAB = BlockParty.BLOCKS.register("ginkgo_slab", () -> new SlabBlock(Prop.NONSOLID.get()));
    public static final RegistryObject<Block> GINKGO_STAIRS = BlockParty.BLOCKS.register("ginkgo_stairs", () -> new StairBlock(() -> GINKGO_PLANKS.get().defaultBlockState(), Prop.NONSOLID.get()));
    public static final RegistryObject<Block> GINKGO_WOOD = BlockParty.BLOCKS.register("ginkgo_wood", () -> new RotatedPillarBlock(Prop.SOLID.get()));
    public static final RegistryObject<Block> GREEN_PAPER_LANTERN = BlockParty.BLOCKS.register("green_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_GREEN));
    public static final RegistryObject<Block> LIGHT_BLUE_PAPER_LANTERN = BlockParty.BLOCKS.register("light_blue_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_LIGHT_BLUE));
    public static final RegistryObject<Block> LIGHT_GRAY_PAPER_LANTERN = BlockParty.BLOCKS.register("light_gray_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_LIGHT_GRAY));
    public static final RegistryObject<Block> LIME_PAPER_LANTERN = BlockParty.BLOCKS.register("lime_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_LIGHT_GREEN));
    public static final RegistryObject<Block> MAGENTA_PAPER_LANTERN = BlockParty.BLOCKS.register("magenta_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_MAGENTA));
    public static final RegistryObject<Block> ORANGE_PAPER_LANTERN = BlockParty.BLOCKS.register("orange_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_ORANGE));
    public static final RegistryObject<Block> PINK_PAPER_LANTERN = BlockParty.BLOCKS.register("pink_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_PINK));
    public static final RegistryObject<Block> PURPLE_PAPER_LANTERN = BlockParty.BLOCKS.register("purple_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_PURPLE));
    public static final RegistryObject<Block> RED_PAPER_LANTERN = BlockParty.BLOCKS.register("red_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_RED));
    public static final RegistryObject<Block> SAKURA_BLOSSOMS = BlockParty.BLOCKS.register("sakura_blossoms", () -> new SakuraBlossomsBlock(CustomParticles.SAKURA, Prop.PLANT.get()));
    public static final RegistryObject<Block> SAKURA_BUTTON = BlockParty.BLOCKS.register("sakura_button", () -> new WoodButtonBlock(Prop.NONSOLID.get()));
    public static final RegistryObject<Block> SAKURA_FENCE = BlockParty.BLOCKS.register("sakura_fence", () -> new FenceBlock(Prop.NONSOLID.get()));
    public static final RegistryObject<Block> SAKURA_FENCE_GATE = BlockParty.BLOCKS.register("sakura_fence_gate", () -> new FenceGateBlock(Prop.NONSOLID.get()));
    public static final RegistryObject<Block> SAKURA_LOG = BlockParty.BLOCKS.register("sakura_log", () -> new RotatedPillarBlock(Prop.SOLID.get()));
    public static final RegistryObject<Block> SAKURA_PLANKS = BlockParty.BLOCKS.register("sakura_planks", () -> new Block(Prop.SOLID.get()));
    public static final RegistryObject<Block> SAKURA_PRESSURE_PLATE = BlockParty.BLOCKS.register("sakura_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, Prop.NONSOLID.get()));
    public static final RegistryObject<Block> SAKURA_SAPLING = BlockParty.BLOCKS.register("sakura_sapling", () -> new SaplingBlock(new SakuraTree(), Prop.PLANT.get()));
    public static final RegistryObject<Block> SAKURA_SLAB = BlockParty.BLOCKS.register("sakura_slab", () -> new SlabBlock(Prop.NONSOLID.get()));
    public static final RegistryObject<Block> SAKURA_STAIRS = BlockParty.BLOCKS.register("sakura_stairs", () -> new StairBlock(() -> SAKURA_PLANKS.get().defaultBlockState(), Prop.NONSOLID.get()));
    public static final RegistryObject<Block> SAKURA_WOOD = BlockParty.BLOCKS.register("sakura_wood", () -> new RotatedPillarBlock(Prop.SOLID.get()));
    public static final RegistryObject<Block> SHOJI_BLOCK = BlockParty.BLOCKS.register("shoji_block", () -> new Block(Prop.SHOJI.get()));
    public static final RegistryObject<Block> SHIMENAWA = BlockParty.BLOCKS.register("shimenawa", () -> new ShimenawaBlock(Prop.TRANSPARENT.get()));
    public static final RegistryObject<Block> SHOJI_LANTERN = BlockParty.BLOCKS.register("shoji_lantern", () -> new ShojiLanternBlock(Prop.SHOJI.get()));
    public static final RegistryObject<Block> SHOJI_PANEL = BlockParty.BLOCKS.register("shoji_panel", () -> new TrapDoorBlock(Prop.SHOJI.get()));
    public static final RegistryObject<Block> SHOJI_SCREEN = BlockParty.BLOCKS.register("shoji_screen", () -> new ShojiScreenBlock(Prop.SHOJI.get()));
    public static final RegistryObject<Block> STRIPPED_GINKGO_LOG = BlockParty.BLOCKS.register("stripped_ginkgo_log", () -> new RotatedPillarBlock(Prop.SOLID.get()));
    public static final RegistryObject<Block> STRIPPED_GINKGO_WOOD = BlockParty.BLOCKS.register("stripped_ginkgo_wood", () -> new RotatedPillarBlock(Prop.SOLID.get()));
    public static final RegistryObject<Block> STRIPPED_SAKURA_LOG = BlockParty.BLOCKS.register("stripped_sakura_log", () -> new RotatedPillarBlock(Prop.SOLID.get()));
    public static final RegistryObject<Block> STRIPPED_SAKURA_WOOD = BlockParty.BLOCKS.register("stripped_sakura_wood", () -> new RotatedPillarBlock(Prop.SOLID.get()));
    public static final RegistryObject<Block> TATAMI_MAT = BlockParty.BLOCKS.register("tatami_mat", () -> new RotatedPillarBlock(Prop.SOLID.get()));
    public static final RegistryObject<Block> SHRINE_TABLET = BlockParty.BLOCKS.register("shrine_tablet", () -> new ShrineTabletBlock(Prop.TRANSPARENT.get()));
    public static final RegistryObject<Block> WHITE_PAPER_LANTERN = BlockParty.BLOCKS.register("white_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_WHITE));
    public static final RegistryObject<Block> WHITE_SAKURA_BLOSSOMS = BlockParty.BLOCKS.register("white_sakura_blossoms", () -> new SakuraBlossomsBlock(CustomParticles.WHITE_SAKURA, Prop.PLANT.get()));
    public static final RegistryObject<Block> WHITE_SAKURA_SAPLING = BlockParty.BLOCKS.register("white_sakura_sapling", () -> new SaplingBlock(new WhiteSakuraTree(), Prop.PLANT.get()));
    public static final RegistryObject<Block> WIND_CHIMES = BlockParty.BLOCKS.register("wind_chimes", () -> new WindChimesBlock(Prop.STONE.get()));
    public static final RegistryObject<Block> WISTERIA_BINE = BlockParty.BLOCKS.register("wisteria_bine", () -> new RotatedPillarBlock(Prop.NONSOLID.get()));
    public static final RegistryObject<Block> WISTERIA_LEAVES = BlockParty.BLOCKS.register("wisteria_leaves", () -> new WisteriaLeavesBlock(Prop.PLANT.get()));
    public static final RegistryObject<Block> WISTERIA_SAPLING = BlockParty.BLOCKS.register("wisteria_sapling", () -> new SaplingBlock(new WisteriaTree(), Prop.PLANT.get()));
    public static final RegistryObject<Block> WISTERIA_VINE_BODY = BlockParty.BLOCKS.register("wisteria_vine_body", () -> new WisteriaVineBodyBlock(Prop.PLANT.get()));
    public static final RegistryObject<Block> WISTERIA_VINE_TIP = BlockParty.BLOCKS.register("wisteria_vine_tip", () -> new WisteriaVineTipBlock(Prop.PLANT.get()));
    public static final RegistryObject<Block> WRITING_TABLE = BlockParty.BLOCKS.register("writing_table", () -> new WritingTableBlock(Prop.SOLID.get()));
    public static final RegistryObject<Block> YELLOW_PAPER_LANTERN = BlockParty.BLOCKS.register("yellow_paper_lantern", () -> new PaperLanternBlock(Prop.SHOJI.get(), MaterialColor.TERRACOTTA_YELLOW));
    public static final RegistryObject<Block> POTTED_GINKGO_SAPLING = BlockParty.BLOCKS.register("potted_ginkgo_sapling", () -> new FlowerPotBlock(CustomBlocks::flowerPot, GINKGO_SAPLING, BlockBehaviour.Properties.copy(flowerPot())));
    public static final RegistryObject<Block> POTTED_SAKURA_SAPLING = BlockParty.BLOCKS.register("potted_sakura_sapling", () -> new FlowerPotBlock(CustomBlocks::flowerPot, SAKURA_SAPLING, BlockBehaviour.Properties.copy(flowerPot())));
    public static final RegistryObject<Block> POTTED_WHITE_SAKURA_SAPLING = BlockParty.BLOCKS.register("potted_white_sakura_sapling", () -> new FlowerPotBlock(CustomBlocks::flowerPot, WHITE_SAKURA_SAPLING, BlockBehaviour.Properties.copy(flowerPot())));
    public static final RegistryObject<Block> POTTED_WISTERIA_SAPLING = BlockParty.BLOCKS.register("potted_wisteria_sapling", () -> new FlowerPotBlock(CustomBlocks::flowerPot, WISTERIA_SAPLING, BlockBehaviour.Properties.copy(flowerPot())));

    public static void add(DeferredRegister<Block> registry, IEventBus bus) {
        bus.addListener(CustomBlocks::registerRenderTypes);
        bus.addListener(CustomBlocks::registerPottedPlants);
        registry.register(bus);
    }

    private static void registerRenderTypes(FMLClientSetupEvent e) {
        ItemBlockRenderTypes.setRenderLayer(BLANK_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BLACK_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(DAWN_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(EVENING_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(MIDNIGHT_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(MORNING_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(NIGHT_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(NOON_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BLUE_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BROWN_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CYAN_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(GARDEN_LANTERN.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(GINKGO_LEAVES.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(GINKGO_SAPLING.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(GRAY_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(GREEN_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(LIGHT_BLUE_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(LIGHT_GRAY_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(LIME_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(MAGENTA_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ORANGE_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(PINK_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SAKURA_BLOSSOMS.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(SAKURA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(POTTED_GINKGO_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(POTTED_SAKURA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(POTTED_WHITE_SAKURA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(POTTED_WISTERIA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(PURPLE_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RED_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SHIMENAWA.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SHOJI_PANEL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SHOJI_SCREEN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SHRINE_TABLET.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WHITE_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WHITE_SAKURA_BLOSSOMS.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(WHITE_SAKURA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WISTERIA_LEAVES.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(WISTERIA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WISTERIA_VINE_BODY.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WISTERIA_VINE_TIP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(YELLOW_PAPER_LANTERN.get(), RenderType.cutout());
    }

    private static void registerPottedPlants(FMLCommonSetupEvent e) {
        flowerPot().addPlant(BlockParty.source("ginkgo_sapling"),
                CustomBlocks.POTTED_GINKGO_SAPLING);
        flowerPot().addPlant(BlockParty.source("sakura_sapling"),
                CustomBlocks.POTTED_SAKURA_SAPLING);
        flowerPot().addPlant(BlockParty.source("white_sakura_sapling"),
                CustomBlocks.POTTED_WHITE_SAKURA_SAPLING);
        flowerPot().addPlant(BlockParty.source("wisteria_sapling"),
                CustomBlocks.POTTED_WISTERIA_SAPLING);
    }

    private static FlowerPotBlock flowerPot() {
        return ((FlowerPotBlock) Blocks.FLOWER_POT);
    }

    private class Prop {
        public static final Supplier<BlockBehaviour.Properties> SOLID = () -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).sound(SoundType.WOOD).strength(3.0F);
        public static final Supplier<BlockBehaviour.Properties> STONE = () -> BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).sound(SoundType.STONE).strength(6.0F);
        public static final Supplier<BlockBehaviour.Properties> SHOJI = () -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK).sound(SoundType.CROP).strength(0.5F).noOcclusion();
        public static final Supplier<BlockBehaviour.Properties> PLANT = () -> BlockBehaviour.Properties.of(Material.PLANT).sound(SoundType.CROP).strength(0.2F).randomTicks().noOcclusion();
        public static final Supplier<BlockBehaviour.Properties> NONSOLID = () -> SOLID.get().noOcclusion();
        public static final Supplier<BlockBehaviour.Properties> TRANSPARENT = () -> NONSOLID.get().isRedstoneConductor((state, reader, pos) -> false);
    }
}
