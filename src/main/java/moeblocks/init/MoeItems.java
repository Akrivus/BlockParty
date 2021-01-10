package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.item.*;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MoeMod.ID);
    public static final RegistryObject<Item> BENTO_BOX = REGISTRY.register("bento_box", BentoBoxItem::new);
    public static final RegistryObject<Item> BLUE_SPIDER_LILY = REGISTRY.register("blue_spider_lily", () -> new MoeBlockItem(MoeBlocks.BLUE_SPIDER_LILY));
    public static final RegistryObject<Item> BRUSH = REGISTRY.register("brush", BrushItem::new);
    public static final RegistryObject<Item> CALLIGRAPHY_TABLE = REGISTRY.register("calligraphy_table", () -> new MoeBlockItem(MoeBlocks.CALLIGRAPHY_TABLE));
    public static final RegistryObject<Item> CELL_PHONE = REGISTRY.register("cell_phone", CellPhoneItem::new);
    public static final RegistryObject<Item> CUPCAKE = REGISTRY.register("cupcake", CupcakeItem::new);
    public static final RegistryObject<Item> INVITE = REGISTRY.register("invite", InviteItem::new);
    public static final RegistryObject<Item> LETTER = REGISTRY.register("letter", LetterItem::new);
    public static final RegistryObject<Item> MOE_DIE = REGISTRY.register("moe_die", MoeDieItem::new);
    public static final RegistryObject<Item> MOE_SPAWN_EGG = REGISTRY.register("moe_spawn_egg", MoeSpawnItem::new);
    public static final RegistryObject<Item> ONIGIRI = REGISTRY.register("onigiri", OnigiriItem::new);
    public static final RegistryObject<Item> PINK_BOW = REGISTRY.register("pink_bow", PinkBowItem::new);
    public static final RegistryObject<Item> PINK_SAKURA_BLOSSOMS = REGISTRY.register("pink_sakura_blossoms", () -> new MoeBlockItem(MoeBlocks.PINK_SAKURA_BLOSSOMS));
    public static final RegistryObject<Item> PINK_SAKURA_SAPLING = REGISTRY.register("pink_sakura_sapling", () -> new MoeBlockItem(MoeBlocks.PINK_SAKURA_SAPLING));
    public static final RegistryObject<Item> RED_SPIDER_LILY = REGISTRY.register("red_spider_lily", () -> new MoeBlockItem(MoeBlocks.RED_SPIDER_LILY));
    public static final RegistryObject<Item> SAKURA_BUTTON = REGISTRY.register("sakura_button", () -> new MoeBlockItem(MoeBlocks.SAKURA_BUTTON));
    public static final RegistryObject<Item> SAKURA_FENCE = REGISTRY.register("sakura_fence", () -> new MoeBlockItem(MoeBlocks.SAKURA_FENCE));
    public static final RegistryObject<Item> SAKURA_FENCE_GATE = REGISTRY.register("sakura_fence_gate", () -> new MoeBlockItem(MoeBlocks.SAKURA_FENCE_GATE));
    public static final RegistryObject<Item> SAKURA_LOG = REGISTRY.register("sakura_log", () -> new MoeBlockItem(MoeBlocks.SAKURA_LOG));
    public static final RegistryObject<Item> SAKURA_PLANKS = REGISTRY.register("sakura_planks", () -> new MoeBlockItem(MoeBlocks.SAKURA_PLANKS));
    public static final RegistryObject<Item> SAKURA_PRESSURE_PLATE = REGISTRY.register("sakura_pressure_plate", () -> new MoeBlockItem(MoeBlocks.SAKURA_PRESSURE_PLATE));
    public static final RegistryObject<Item> SAKURA_SLAB = REGISTRY.register("sakura_slab", () -> new MoeBlockItem(MoeBlocks.SAKURA_SLAB));
    public static final RegistryObject<Item> SAKURA_STAIRS = REGISTRY.register("sakura_stairs", () -> new MoeBlockItem(MoeBlocks.SAKURA_STAIRS));
    public static final RegistryObject<Item> SAKURA_WOOD = REGISTRY.register("sakura_wood", () -> new MoeBlockItem(MoeBlocks.SAKURA_WOOD));
    public static final RegistryObject<Item> SHOJI_BLOCK = REGISTRY.register("shoji_block", () -> new MoeBlockItem(MoeBlocks.SHOJI_BLOCK));
    public static final RegistryObject<Item> SHOJI_LAMP = REGISTRY.register("shoji_lamp", () -> new MoeBlockItem(MoeBlocks.SHOJI_LAMP));
    public static final RegistryObject<Item> SHOJI_PANEL = REGISTRY.register("shoji_panel", () -> new MoeBlockItem(MoeBlocks.SHOJI_PANEL));
    public static final RegistryObject<Item> SHOJI_SCREEN = REGISTRY.register("shoji_screen", () -> new MoeBlockItem(MoeBlocks.SHOJI_SCREEN));
    public static final RegistryObject<Item> STRIPPED_SAKURA_LOG = REGISTRY.register("stripped_sakura_log", () -> new MoeBlockItem(MoeBlocks.STRIPPED_SAKURA_LOG));
    public static final RegistryObject<Item> STRIPPED_SAKURA_WOOD = REGISTRY.register("stripped_sakura_wood", () -> new MoeBlockItem(MoeBlocks.STRIPPED_SAKURA_WOOD));
    public static final RegistryObject<Item> TATAMI_MAT = REGISTRY.register("tatami_mat", () -> new MoeBlockItem(MoeBlocks.TATAMI_MAT));
    public static final RegistryObject<Item> WHITE_SAKURA_BLOSSOMS = REGISTRY.register("white_sakura_blossoms", () -> new MoeBlockItem(MoeBlocks.WHITE_SAKURA_BLOSSOMS));
    public static final RegistryObject<Item> WHITE_SAKURA_SAPLING = REGISTRY.register("white_sakura_sapling", () -> new MoeBlockItem(MoeBlocks.WHITE_SAKURA_SAPLING));
    public static final RegistryObject<Item> WISTERIA_LEAVES = REGISTRY.register("wisteria_leaves", () -> new MoeBlockItem(MoeBlocks.WISTERIA_LEAVES));
    public static final RegistryObject<Item> WISTERIA_SAPLING = REGISTRY.register("wisteria_sapling", () -> new MoeBlockItem(MoeBlocks.WISTERIA_SAPLING));
    public static final RegistryObject<Item> WISTERIA_BINE = REGISTRY.register("wisteria_bine", () -> new MoeBlockItem(MoeBlocks.WISTERIA_BINE));
    public static final RegistryObject<Item> WISTERIA_VINES = REGISTRY.register("wisteria_vines", () -> new MoeBlockItem(MoeBlocks.WISTERIA_VINE_TIP));
    public static final RegistryObject<Item> YEARBOOK = REGISTRY.register("yearbook", YearbookItem::new);
    public static final RegistryObject<Item> YEARBOOK_PAGE = REGISTRY.register("yearbook_page", YearbookPageItem::new);

    public static void registerDispenserBehaviors() {
        DispenserBlock.registerDispenseBehavior(MoeItems.MOE_DIE.get(), MoeDieItem.DISPENSER_BEHAVIOR);
    }
    
    public static void registerModelProperties() {
        ItemModelsProperties.registerProperty(MoeItems.INVITE.get(), LetterItem.CLOSED_PROPERTY, LetterItem.CLOSED_PROPERTY_GETTER);
        ItemModelsProperties.registerProperty(MoeItems.LETTER.get(), LetterItem.CLOSED_PROPERTY, LetterItem.CLOSED_PROPERTY_GETTER);
    }
}
