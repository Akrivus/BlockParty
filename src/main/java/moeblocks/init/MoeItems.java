package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.item.*;
import moeblocks.util.sort.ISortableItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MoeMod.ID);
    public static final RegistryObject<Item> BENTO_BOX = REGISTRY.register("bento_box", BentoBoxItem::new);
    public static final RegistryObject<Item> BLACK_PAPER_LANTERN = REGISTRY.register("black_paper_lantern", () -> new MoeBlockItem(MoeBlocks.BLACK_PAPER_LANTERN));
    public static final RegistryObject<Item> BLANK_HANGING_SCROLL = REGISTRY.register("blank_hanging_scroll", () -> new MoeBlockItem(MoeBlocks.BLANK_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> BLUE_PAPER_LANTERN = REGISTRY.register("blue_paper_lantern", () -> new MoeBlockItem(MoeBlocks.BLUE_PAPER_LANTERN));
    public static final RegistryObject<Item> BLUE_SPIDER_LILY = REGISTRY.register("blue_spider_lily", () -> new MoeBlockItem(MoeBlocks.BLUE_SPIDER_LILY, 10));
    public static final RegistryObject<Item> BROWN_PAPER_LANTERN = REGISTRY.register("brown_paper_lantern", () -> new MoeBlockItem(MoeBlocks.BROWN_PAPER_LANTERN));
    public static final RegistryObject<Item> CALLIGRAPHY_BRUSH = REGISTRY.register("calligraphy_brush", CalligraphyBrushItem::new);
    public static final RegistryObject<Item> CELL_PHONE = REGISTRY.register("cell_phone", CellPhoneItem::new);
    public static final RegistryObject<Item> CUPCAKE = REGISTRY.register("cupcake", CupcakeItem::new);
    public static final RegistryObject<Item> CYAN_PAPER_LANTERN = REGISTRY.register("cyan_paper_lantern", () -> new MoeBlockItem(MoeBlocks.CYAN_PAPER_LANTERN));
    public static final RegistryObject<Item> DAWN_HANGING_SCROLL = REGISTRY.register("dawn_hanging_scroll", () -> new MoeBlockItem(MoeBlocks.DAWN_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> DEER_SPAWN_EGG = REGISTRY.register("deer_spawn_egg", () -> new SpawnMobItem(() -> MoeEntities.DEER.get(), 0xc17f3e, 0xdbb47f));
    public static final RegistryObject<Item> EVENING_HANGING_SCROLL = REGISTRY.register("evening_hanging_scroll", () -> new MoeBlockItem(MoeBlocks.EVENING_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> GARDEN_LANTERN = REGISTRY.register("garden_lantern", () -> new MoeBlockItem(MoeBlocks.GARDEN_LANTERN, 5));
    public static final RegistryObject<Item> GRAY_PAPER_LANTERN = REGISTRY.register("gray_paper_lantern", () -> new MoeBlockItem(MoeBlocks.GRAY_PAPER_LANTERN));
    public static final RegistryObject<Item> GREEN_PAPER_LANTERN = REGISTRY.register("green_paper_lantern", () -> new MoeBlockItem(MoeBlocks.GREEN_PAPER_LANTERN));
    public static final RegistryObject<Item> LETTER = REGISTRY.register("letter", LetterItem::new);
    public static final RegistryObject<Item> LIGHT_BLUE_PAPER_LANTERN = REGISTRY.register("light_blue_paper_lantern", () -> new MoeBlockItem(MoeBlocks.LIGHT_BLUE_PAPER_LANTERN));
    public static final RegistryObject<Item> LIGHT_GRAY_PAPER_LANTERN = REGISTRY.register("light_gray_paper_lantern", () -> new MoeBlockItem(MoeBlocks.LIGHT_GRAY_PAPER_LANTERN));
    public static final RegistryObject<Item> LIME_PAPER_LANTERN = REGISTRY.register("lime_paper_lantern", () -> new MoeBlockItem(MoeBlocks.LIME_PAPER_LANTERN));
    public static final RegistryObject<Item> MAGENTA_PAPER_LANTERN = REGISTRY.register("magenta_paper_lantern", () -> new MoeBlockItem(MoeBlocks.MAGENTA_PAPER_LANTERN));
    public static final RegistryObject<Item> MIDNIGHT_HANGING_SCROLL = REGISTRY.register("midnight_hanging_scroll", () -> new MoeBlockItem(MoeBlocks.MIDNIGHT_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> MOE_DIE = REGISTRY.register("moe_die", MoeDieItem::new);
    public static final RegistryObject<Item> MOE_SPAWN_EGG = REGISTRY.register("moe_spawn_egg", SpawnMoeItem::new);
    public static final RegistryObject<Item> MORNING_HANGING_SCROLL = REGISTRY.register("morning_hanging_scroll", () -> new MoeBlockItem(MoeBlocks.MORNING_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> NIGHT_HANGING_SCROLL = REGISTRY.register("night_hanging_scroll", () -> new MoeBlockItem(MoeBlocks.NIGHT_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> NOON_HANGING_SCROLL = REGISTRY.register("noon_hanging_scroll", () -> new MoeBlockItem(MoeBlocks.NOON_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> ONIGIRI = REGISTRY.register("onigiri", OnigiriItem::new);
    public static final RegistryObject<Item> ORANGE_PAPER_LANTERN = REGISTRY.register("orange_paper_lantern", () -> new MoeBlockItem(MoeBlocks.ORANGE_PAPER_LANTERN));
    public static final RegistryObject<Item> PINK_BOW = REGISTRY.register("pink_bow", PinkBowItem::new);
    public static final RegistryObject<Item> PINK_PAPER_LANTERN = REGISTRY.register("pink_paper_lantern", () -> new MoeBlockItem(MoeBlocks.PINK_PAPER_LANTERN));
    public static final RegistryObject<Item> PINK_SAKURA_BLOSSOMS = REGISTRY.register("pink_sakura_blossoms", () -> new MoeBlockItem(MoeBlocks.PINK_SAKURA_BLOSSOMS, 10));
    public static final RegistryObject<Item> PINK_SAKURA_SAPLING = REGISTRY.register("pink_sakura_sapling", () -> new MoeBlockItem(MoeBlocks.PINK_SAKURA_SAPLING, 10));
    public static final RegistryObject<Item> PURPLE_PAPER_LANTERN = REGISTRY.register("purple_paper_lantern", () -> new MoeBlockItem(MoeBlocks.PURPLE_PAPER_LANTERN));
    public static final RegistryObject<Item> RED_PAPER_LANTERN = REGISTRY.register("red_paper_lantern", () -> new MoeBlockItem(MoeBlocks.RED_PAPER_LANTERN));
    public static final RegistryObject<Item> RED_SPIDER_LILY = REGISTRY.register("red_spider_lily", () -> new MoeBlockItem(MoeBlocks.RED_SPIDER_LILY, 10));
    public static final RegistryObject<Item> SAKURA_BUTTON = REGISTRY.register("sakura_button", () -> new MoeBlockItem(MoeBlocks.SAKURA_BUTTON, 20));
    public static final RegistryObject<Item> SAKURA_FENCE = REGISTRY.register("sakura_fence", () -> new MoeBlockItem(MoeBlocks.SAKURA_FENCE, 20));
    public static final RegistryObject<Item> SAKURA_FENCE_GATE = REGISTRY.register("sakura_fence_gate", () -> new MoeBlockItem(MoeBlocks.SAKURA_FENCE_GATE, 20));
    public static final RegistryObject<Item> SAKURA_LOG = REGISTRY.register("sakura_log", () -> new MoeBlockItem(MoeBlocks.SAKURA_LOG, 20));
    public static final RegistryObject<Item> SAKURA_PLANKS = REGISTRY.register("sakura_planks", () -> new MoeBlockItem(MoeBlocks.SAKURA_PLANKS, 20));
    public static final RegistryObject<Item> SAKURA_PRESSURE_PLATE = REGISTRY.register("sakura_pressure_plate", () -> new MoeBlockItem(MoeBlocks.SAKURA_PRESSURE_PLATE, 20));
    public static final RegistryObject<Item> SAKURA_SLAB = REGISTRY.register("sakura_slab", () -> new MoeBlockItem(MoeBlocks.SAKURA_SLAB, 20));
    public static final RegistryObject<Item> SAKURA_STAIRS = REGISTRY.register("sakura_stairs", () -> new MoeBlockItem(MoeBlocks.SAKURA_STAIRS, 20));
    public static final RegistryObject<Item> SAKURA_WOOD = REGISTRY.register("sakura_wood", () -> new MoeBlockItem(MoeBlocks.SAKURA_WOOD, 20));
    public static final RegistryObject<Item> SHIMENAWA = REGISTRY.register("shimenawa", () -> new MoeBlockItem(MoeBlocks.SHIMENAWA, 6));
    public static final RegistryObject<Item> SHOJI_BLOCK = REGISTRY.register("shoji_block", () -> new MoeBlockItem(MoeBlocks.SHOJI_BLOCK, 20));
    public static final RegistryObject<Item> SHOJI_LANTERN = REGISTRY.register("shoji_lantern", () -> new MoeBlockItem(MoeBlocks.SHOJI_LANTERN, 20));
    public static final RegistryObject<Item> SHOJI_PANEL = REGISTRY.register("shoji_panel", () -> new MoeBlockItem(MoeBlocks.SHOJI_PANEL, 20));
    public static final RegistryObject<Item> SHOJI_SCREEN = REGISTRY.register("shoji_screen", () -> new MoeBlockItem(MoeBlocks.SHOJI_SCREEN, 20));
    public static final RegistryObject<Item> STRIPPED_SAKURA_LOG = REGISTRY.register("stripped_sakura_log", () -> new MoeBlockItem(MoeBlocks.STRIPPED_SAKURA_LOG, 20));
    public static final RegistryObject<Item> STRIPPED_SAKURA_WOOD = REGISTRY.register("stripped_sakura_wood", () -> new MoeBlockItem(MoeBlocks.STRIPPED_SAKURA_WOOD, 20));
    public static final RegistryObject<Item> TATAMI_MAT = REGISTRY.register("tatami_mat", () -> new MoeBlockItem(MoeBlocks.TATAMI_MAT, 20));
    public static final RegistryObject<Item> TORII_TABLET = REGISTRY.register("torii_tablet", () -> new MoeBlockItem(MoeBlocks.TORII_TABLET, 5));
    public static final RegistryObject<Item> WHITE_PAPER_LANTERN = REGISTRY.register("white_paper_lantern", () -> new MoeBlockItem(MoeBlocks.WHITE_PAPER_LANTERN));
    public static final RegistryObject<Item> WHITE_SAKURA_BLOSSOMS = REGISTRY.register("white_sakura_blossoms", () -> new MoeBlockItem(MoeBlocks.WHITE_SAKURA_BLOSSOMS, 10));
    public static final RegistryObject<Item> WHITE_SAKURA_SAPLING = REGISTRY.register("white_sakura_sapling", () -> new MoeBlockItem(MoeBlocks.WHITE_SAKURA_SAPLING, 10));
    public static final RegistryObject<Item> WISTERIA_BINE = REGISTRY.register("wisteria_bine", () -> new MoeBlockItem(MoeBlocks.WISTERIA_BINE, 20));
    public static final RegistryObject<Item> WISTERIA_LEAVES = REGISTRY.register("wisteria_leaves", () -> new MoeBlockItem(MoeBlocks.WISTERIA_LEAVES, 20));
    public static final RegistryObject<Item> WISTERIA_SAPLING = REGISTRY.register("wisteria_sapling", () -> new MoeBlockItem(MoeBlocks.WISTERIA_SAPLING, 20));
    public static final RegistryObject<Item> WISTERIA_VINES = REGISTRY.register("wisteria_vines", () -> new MoeBlockItem(MoeBlocks.WISTERIA_VINE_TIP, 20));
    public static final RegistryObject<Item> WRITING_TABLE = REGISTRY.register("writing_table", () -> new MoeBlockItem(MoeBlocks.WRITING_TABLE, 5));
    public static final RegistryObject<Item> YEARBOOK = REGISTRY.register("yearbook", YearbookItem::new);
    public static final RegistryObject<Item> YEARBOOK_PAGE = REGISTRY.register("yearbook_page", YearbookPageItem::new);
    public static final RegistryObject<Item> YELLOW_PAPER_LANTERN = REGISTRY.register("yellow_paper_lantern", () -> new MoeBlockItem(MoeBlocks.YELLOW_PAPER_LANTERN));
    public static final RegistryObject<Item> YELLOW_SAKURA_BLOSSOMS = REGISTRY.register("yellow_sakura_blossoms", () -> new MoeBlockItem(MoeBlocks.YELLOW_SAKURA_BLOSSOMS, 10));

    public static void registerDispenserBehaviors() {
        DispenserBlock.registerDispenseBehavior(MoeItems.MOE_DIE.get(), MoeDieItem.DISPENSER_BEHAVIOR);
    }

    public static void registerModelProperties() {
        ItemModelsProperties.registerProperty(MoeItems.LETTER.get(), new ResourceLocation("closed"), (stack, world, entity) -> LetterItem.isClosed(stack));
    }

    public static int compare(ItemStack one, ItemStack two) {
        Item item1 = one.getItem();
        Item item2 = two.getItem();
        String name1 = item1.getRegistryName().getPath();
        String name2 = item2.getRegistryName().getPath();
        if (item1 instanceof ISortableItem && item2 instanceof ISortableItem) {
            int sort1 = ((ISortableItem) item1).getSortOrder();
            int sort2 = ((ISortableItem) item2).getSortOrder();
            int sorts = Integer.compare(sort1, sort2);
            if (sorts == 0) {
                return name1.compareToIgnoreCase(name2);
            } else {
                return sorts;
            }
        } else if (item1 instanceof ISortableItem) {
            return 1;
        } else if (item2 instanceof ISortableItem) {
            return -1;
        } else {
            return name1.compareToIgnoreCase(name2);
        }
    }
}
