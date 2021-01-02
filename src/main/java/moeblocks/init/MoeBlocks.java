package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.block.MoeFlowerBlock;
import moeblocks.block.MoeLeavesBlock;
import moeblocks.block.tree.SakuraTree;
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
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MoeMod.ID);
    public static final RegistryObject<Block> BLUE_SPIDER_LILY = REGISTRY.register("blue_spider_lily", () -> new MoeFlowerBlock(Effects.BAD_OMEN));
    public static final RegistryObject<Block> RED_SPIDER_LILY = REGISTRY.register("red_spider_lily", () -> new MoeFlowerBlock(Effects.HERO_OF_THE_VILLAGE));
    public static final RegistryObject<Block> SAKURA_BLOSSOMS = REGISTRY.register("sakura_blossoms", () -> new MoeLeavesBlock(MaterialColor.PINK));
    public static final RegistryObject<Block> SAKURA_BUTTON = REGISTRY.register("sakura_button", () -> new WoodButtonBlock(AbstractBlock.Properties.create(Material.WOOD, MaterialColor.WOOD).sound(SoundType.WOOD).hardnessAndResistance(3.0F).notSolid()));
    public static final RegistryObject<Block> SAKURA_DOOR = REGISTRY.register("sakura_door", () -> new DoorBlock(AbstractBlock.Properties.from(SAKURA_BUTTON.get())));
    public static final RegistryObject<Block> SAKURA_FENCE = REGISTRY.register("sakura_fence", () -> new FenceBlock(AbstractBlock.Properties.from(SAKURA_BUTTON.get())));
    public static final RegistryObject<Block> SAKURA_FENCE_GATE = REGISTRY.register("sakura_fence_gate", () -> new FenceGateBlock(AbstractBlock.Properties.from(SAKURA_BUTTON.get())));
    public static final RegistryObject<Block> SAKURA_LEAVES = REGISTRY.register("sakura_leaves", () -> new MoeLeavesBlock(MaterialColor.PINK));
    public static final RegistryObject<Block> SAKURA_LOG = REGISTRY.register("sakura_log", () -> new RotatedPillarBlock(AbstractBlock.Properties.from(SAKURA_BUTTON.get())));
    public static final RegistryObject<Block> SAKURA_PLANKS = REGISTRY.register("sakura_planks", () -> new Block(AbstractBlock.Properties.from(SAKURA_BUTTON.get())));
    public static final RegistryObject<Block> SAKURA_PRESSURE_PLATE = REGISTRY.register("sakura_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, AbstractBlock.Properties.from(SAKURA_BUTTON.get())));
    public static final RegistryObject<Block> SAKURA_SAPLING = REGISTRY.register("sakura_sapling", () -> new SaplingBlock(new SakuraTree(), AbstractBlock.Properties.from(SAKURA_LEAVES.get()).zeroHardnessAndResistance().doesNotBlockMovement()));
    public static final RegistryObject<Block> SAKURA_SLAB = REGISTRY.register("sakura_slab", () -> new SlabBlock(AbstractBlock.Properties.from(SAKURA_BUTTON.get())));
    public static final RegistryObject<Block> SAKURA_STAIRS = REGISTRY.register("sakura_stairs", () -> new StairsBlock(() -> SAKURA_PLANKS.get().getDefaultState(), AbstractBlock.Properties.from(SAKURA_BUTTON.get())));
    public static final RegistryObject<Block> SAKURA_TRAPDOOR = REGISTRY.register("sakura_trapdoor", () -> new TrapDoorBlock(AbstractBlock.Properties.from(SAKURA_BUTTON.get())));
    public static final RegistryObject<Block> SAKURA_WOOD = REGISTRY.register("sakura_wood", () -> new RotatedPillarBlock(AbstractBlock.Properties.from(SAKURA_BUTTON.get())));
    public static final RegistryObject<Block> STRIPPED_SAKURA_LOG = REGISTRY.register("stripped_sakura_log", () -> new RotatedPillarBlock(AbstractBlock.Properties.from(SAKURA_BUTTON.get())));
    public static final RegistryObject<Block> STRIPPED_SAKURA_WOOD = REGISTRY.register("stripped_sakura_wood", () -> new RotatedPillarBlock(AbstractBlock.Properties.from(SAKURA_BUTTON.get())));
    public static final RegistryObject<Block> POTTED_BLUE_SPIDER_LILY = REGISTRY.register("potted_blue_spider_lily", () -> new FlowerPotBlock(MoeBlocks::getFlowerPot, BLUE_SPIDER_LILY, AbstractBlock.Properties.from(MoeBlocks.getFlowerPot())));
    public static final RegistryObject<Block> POTTED_RED_SPIDER_LILY = REGISTRY.register("potted_red_spider_lily", () -> new FlowerPotBlock(MoeBlocks::getFlowerPot, RED_SPIDER_LILY, AbstractBlock.Properties.from(MoeBlocks.getFlowerPot())));
    public static final RegistryObject<Block> POTTED_SAKURA_SAPLING = REGISTRY.register("potted_sakura_sapling", () -> new FlowerPotBlock(MoeBlocks::getFlowerPot, SAKURA_SAPLING, AbstractBlock.Properties.from(MoeBlocks.getFlowerPot())));

    public static void registerPottedPlants() {
        getFlowerPot().addPlant(BLUE_SPIDER_LILY.get().getRegistryName(), POTTED_BLUE_SPIDER_LILY);
        getFlowerPot().addPlant(RED_SPIDER_LILY.get().getRegistryName(), POTTED_RED_SPIDER_LILY);
        getFlowerPot().addPlant(SAKURA_SAPLING.get().getRegistryName(), POTTED_SAKURA_SAPLING);
    }

    public static void registerRenderTypes() {
        RenderTypeLookup.setRenderLayer(BLUE_SPIDER_LILY.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(RED_SPIDER_LILY.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(SAKURA_SAPLING.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(POTTED_BLUE_SPIDER_LILY.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(POTTED_RED_SPIDER_LILY.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(POTTED_SAKURA_SAPLING.get(), RenderType.getCutout());
    }

    public static FlowerPotBlock getFlowerPot() {
        return (FlowerPotBlock) Blocks.FLOWER_POT;
    }
}
