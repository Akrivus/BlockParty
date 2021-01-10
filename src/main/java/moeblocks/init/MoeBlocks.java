package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.block.*;
import moeblocks.block.tree.BaseSakuraTree;
import moeblocks.block.tree.WisteriaTree;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeBlocks {
    public static final AbstractBlock.Properties SAKURA_WOOD_PROPERTIES = AbstractBlock.Properties.create(Material.WOOD, MaterialColor.WOOD).sound(SoundType.WOOD).hardnessAndResistance(3.0F);
    public static final AbstractBlock.Properties SHOJI_BLOCK_PROPERTIES = AbstractBlock.Properties.create(Material.WOOD, MaterialColor.PINK).sound(SoundType.CROP).hardnessAndResistance(1.0F).notSolid();
    public static final AbstractBlock.Properties MOE_PLANT_PROPERTIES = AbstractBlock.Properties.create(Material.PLANTS).sound(SoundType.CROP).hardnessAndResistance(0.2F).tickRandomly();
    public static final AbstractBlock.Properties MOE_NONSOLID_PROPERTIES = AbstractBlock.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(3.0F).notSolid();
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MoeMod.ID);
    public static final RegistryObject<Block> CALLIGRAPHY_TABLE = REGISTRY.register("calligraphy_table", () -> new CalligraphyTableBlock(SAKURA_WOOD_PROPERTIES));
    public static final RegistryObject<Block> PINK_SAKURA_BLOSSOMS = REGISTRY.register("pink_sakura_blossoms", () -> new SakuraBlossomsBlock(MOE_PLANT_PROPERTIES));
    public static final RegistryObject<Block> PINK_SAKURA_SAPLING = REGISTRY.register("pink_sakura_sapling", () -> new SaplingBlock(new BaseSakuraTree(PINK_SAKURA_BLOSSOMS), MOE_PLANT_PROPERTIES));
    public static final RegistryObject<Block> WHITE_SAKURA_BLOSSOMS = REGISTRY.register("white_sakura_blossoms", () -> new SakuraBlossomsBlock(MOE_PLANT_PROPERTIES));
    public static final RegistryObject<Block> WHITE_SAKURA_SAPLING = REGISTRY.register("white_sakura_sapling", () -> new SaplingBlock(new BaseSakuraTree(WHITE_SAKURA_BLOSSOMS), MOE_PLANT_PROPERTIES));
    public static final RegistryObject<Block> WISTERIA_SAPLING = REGISTRY.register("wisteria_sapling", () -> new SaplingBlock(new WisteriaTree(), MOE_PLANT_PROPERTIES));
    public static final RegistryObject<Block> BLUE_SPIDER_LILY = REGISTRY.register("blue_spider_lily", () -> new MoeFlowerBlock(MOE_PLANT_PROPERTIES, Effects.BAD_OMEN));
    public static final RegistryObject<Block> RED_SPIDER_LILY = REGISTRY.register("red_spider_lily", () -> new MoeFlowerBlock(MOE_PLANT_PROPERTIES, Effects.HERO_OF_THE_VILLAGE));
    public static final RegistryObject<Block> POTTED_BLUE_SPIDER_LILY = REGISTRY.register("potted_blue_spider_lily", () -> new FlowerPotBlock(MoeBlocks::getFlowerPot, BLUE_SPIDER_LILY, AbstractBlock.Properties.from(MoeBlocks.getFlowerPot())));
    public static final RegistryObject<Block> POTTED_PINK_SAKURA_SAPLING = REGISTRY.register("potted_pink_sakura_sapling", () -> new FlowerPotBlock(MoeBlocks::getFlowerPot, PINK_SAKURA_SAPLING, AbstractBlock.Properties.from(MoeBlocks.getFlowerPot())));
    public static final RegistryObject<Block> POTTED_RED_SPIDER_LILY = REGISTRY.register("potted_red_spider_lily", () -> new FlowerPotBlock(MoeBlocks::getFlowerPot, RED_SPIDER_LILY, AbstractBlock.Properties.from(MoeBlocks.getFlowerPot())));
    public static final RegistryObject<Block> POTTED_WHITE_SAKURA_SAPLING = REGISTRY.register("potted_white_sakura_sapling", () -> new FlowerPotBlock(MoeBlocks::getFlowerPot, WHITE_SAKURA_SAPLING, AbstractBlock.Properties.from(MoeBlocks.getFlowerPot())));
    public static final RegistryObject<Block> POTTED_WISTERIA_SAPLING = REGISTRY.register("potted_wisteria_sapling", () -> new FlowerPotBlock(MoeBlocks::getFlowerPot, WISTERIA_SAPLING, AbstractBlock.Properties.from(MoeBlocks.getFlowerPot())));
    public static final RegistryObject<Block> SAKURA_BUTTON = REGISTRY.register("sakura_button", () -> new WoodButtonBlock(MOE_NONSOLID_PROPERTIES));
    public static final RegistryObject<Block> SAKURA_FENCE = REGISTRY.register("sakura_fence", () -> new FenceBlock(MOE_NONSOLID_PROPERTIES));
    public static final RegistryObject<Block> SAKURA_FENCE_GATE = REGISTRY.register("sakura_fence_gate", () -> new FenceGateBlock(MOE_NONSOLID_PROPERTIES));
    public static final RegistryObject<Block> SAKURA_LOG = REGISTRY.register("sakura_log", () -> new RotatedPillarBlock(SAKURA_WOOD_PROPERTIES));
    public static final RegistryObject<Block> SAKURA_PLANKS = REGISTRY.register("sakura_planks", () -> new Block(SAKURA_WOOD_PROPERTIES));
    public static final RegistryObject<Block> SAKURA_PRESSURE_PLATE = REGISTRY.register("sakura_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, MOE_NONSOLID_PROPERTIES));
    public static final RegistryObject<Block> SAKURA_SLAB = REGISTRY.register("sakura_slab", () -> new SlabBlock(MOE_NONSOLID_PROPERTIES));
    public static final RegistryObject<Block> SAKURA_STAIRS = REGISTRY.register("sakura_stairs", () -> new StairsBlock(() -> SAKURA_PLANKS.get().getDefaultState(), MOE_NONSOLID_PROPERTIES));
    public static final RegistryObject<Block> SAKURA_WOOD = REGISTRY.register("sakura_wood", () -> new RotatedPillarBlock(SAKURA_WOOD_PROPERTIES));
    public static final RegistryObject<Block> SHOJI_BLOCK = REGISTRY.register("shoji_block", () -> new Block(SHOJI_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> SHOJI_LAMP = REGISTRY.register("shoji_lamp", () -> new ShojiLampBlock(SHOJI_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> SHOJI_PANEL = REGISTRY.register("shoji_panel", () -> new TrapDoorBlock(SHOJI_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> SHOJI_SCREEN = REGISTRY.register("shoji_screen", () -> new ShojiScreenBlock(SHOJI_BLOCK_PROPERTIES));
    public static final RegistryObject<Block> STRIPPED_SAKURA_LOG = REGISTRY.register("stripped_sakura_log", () -> new RotatedPillarBlock(SAKURA_WOOD_PROPERTIES));
    public static final RegistryObject<Block> STRIPPED_SAKURA_WOOD = REGISTRY.register("stripped_sakura_wood", () -> new RotatedPillarBlock(SAKURA_WOOD_PROPERTIES));
    public static final RegistryObject<Block> TATAMI_MAT = REGISTRY.register("tatami_mat", () -> new RotatedPillarBlock(SAKURA_WOOD_PROPERTIES));
    public static final RegistryObject<Block> WISTERIA_LEAVES = REGISTRY.register("wisteria_leaves", () -> new WisteriaLeavesBlock(MOE_PLANT_PROPERTIES));
    public static final RegistryObject<Block> WISTERIA_BINE = REGISTRY.register("wisteria_bine", () -> new RotatedPillarBlock(MOE_NONSOLID_PROPERTIES));
    public static final RegistryObject<Block> WISTERIA_VINE_BODY = REGISTRY.register("wisteria_vine_body", () -> new WisteriaVineBodyBlock(MOE_PLANT_PROPERTIES));
    public static final RegistryObject<Block> WISTERIA_VINE_TIP = REGISTRY.register("wisteria_vine_tip", () -> new WisteriaVineTipBlock(MOE_PLANT_PROPERTIES));

    public static void registerPottedPlants() {
        getFlowerPot().addPlant(BLUE_SPIDER_LILY.get().getRegistryName(), POTTED_BLUE_SPIDER_LILY);
        getFlowerPot().addPlant(PINK_SAKURA_SAPLING.get().getRegistryName(), POTTED_PINK_SAKURA_SAPLING);
        getFlowerPot().addPlant(RED_SPIDER_LILY.get().getRegistryName(), POTTED_RED_SPIDER_LILY);
        getFlowerPot().addPlant(WHITE_SAKURA_SAPLING.get().getRegistryName(), POTTED_WHITE_SAKURA_SAPLING);
        getFlowerPot().addPlant(WISTERIA_SAPLING.get().getRegistryName(), POTTED_WISTERIA_SAPLING);
    }

    public static void registerRenderTypes() {
        RenderTypeLookup.setRenderLayer(BLUE_SPIDER_LILY.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(PINK_SAKURA_BLOSSOMS.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(PINK_SAKURA_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(POTTED_BLUE_SPIDER_LILY.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(POTTED_PINK_SAKURA_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(POTTED_RED_SPIDER_LILY.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(POTTED_WHITE_SAKURA_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(POTTED_WISTERIA_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RED_SPIDER_LILY.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(SHOJI_PANEL.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(SHOJI_SCREEN.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(WHITE_SAKURA_BLOSSOMS.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(WHITE_SAKURA_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(WISTERIA_LEAVES.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(WISTERIA_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(WISTERIA_VINE_BODY.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(WISTERIA_VINE_TIP.get(), RenderType.getCutout());
    }

    private static FlowerPotBlock getFlowerPot() {
        return (FlowerPotBlock) Blocks.FLOWER_POT;
    }
}
