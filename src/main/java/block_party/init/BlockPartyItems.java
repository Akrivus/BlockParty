package block_party.init;

import block_party.items.*;
import block_party.util.sort.ISortableItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockPartyItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, block_party.BlockParty.ID);
    public static final RegistryObject<Item> BENTO_BOX = REGISTRY.register("bento_box", BentoBoxItem::new);
    public static final RegistryObject<Item> BLACK_PAPER_LANTERN = REGISTRY.register("black_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.BLACK_PAPER_LANTERN));
    public static final RegistryObject<Item> BLANK_HANGING_SCROLL = REGISTRY.register("blank_hanging_scroll", () -> new MoeBlockItem(BlockPartyBlocks.BLANK_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> BLUE_PAPER_LANTERN = REGISTRY.register("blue_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.BLUE_PAPER_LANTERN));
    public static final RegistryObject<Item> BROWN_PAPER_LANTERN = REGISTRY.register("brown_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.BROWN_PAPER_LANTERN));
    public static final RegistryObject<Item> CALLIGRAPHY_BRUSH = REGISTRY.register("calligraphy_brush", CalligraphyBrushItem::new);
    public static final RegistryObject<Item> CELL_PHONE = REGISTRY.register("cell_phone", CellPhoneItem::new);
    public static final RegistryObject<Item> CUPCAKE = REGISTRY.register("cupcake", CupcakeItem::new);
    public static final RegistryObject<Item> CYAN_PAPER_LANTERN = REGISTRY.register("cyan_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.CYAN_PAPER_LANTERN));
    public static final RegistryObject<Item> DAWN_HANGING_SCROLL = REGISTRY.register("dawn_hanging_scroll", () -> new MoeBlockItem(BlockPartyBlocks.DAWN_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> EVENING_HANGING_SCROLL = REGISTRY.register("evening_hanging_scroll", () -> new MoeBlockItem(BlockPartyBlocks.EVENING_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> GARDEN_LANTERN = REGISTRY.register("garden_lantern", () -> new MoeBlockItem(BlockPartyBlocks.GARDEN_LANTERN, 5));
    public static final RegistryObject<Item> GINKGO_BUTTON = REGISTRY.register("ginkgo_button", () -> new MoeBlockItem(BlockPartyBlocks.GINKGO_BUTTON, 20));
    public static final RegistryObject<Item> GINKGO_FENCE = REGISTRY.register("ginkgo_fence", () -> new MoeBlockItem(BlockPartyBlocks.GINKGO_FENCE, 20));
    public static final RegistryObject<Item> GINKGO_FENCE_GATE = REGISTRY.register("ginkgo_fence_gate", () -> new MoeBlockItem(BlockPartyBlocks.GINKGO_FENCE_GATE, 20));
    public static final RegistryObject<Item> GINKGO_LEAVES = REGISTRY.register("ginkgo_leaves", () -> new MoeBlockItem(BlockPartyBlocks.GINKGO_LEAVES, 10));
    public static final RegistryObject<Item> GINKGO_LOG = REGISTRY.register("ginkgo_log", () -> new MoeBlockItem(BlockPartyBlocks.GINKGO_LOG, 20));
    public static final RegistryObject<Item> GINKGO_PLANKS = REGISTRY.register("ginkgo_planks", () -> new MoeBlockItem(BlockPartyBlocks.GINKGO_PLANKS, 20));
    public static final RegistryObject<Item> GINKGO_PRESSURE_PLATE = REGISTRY.register("ginkgo_pressure_plate", () -> new MoeBlockItem(BlockPartyBlocks.GINKGO_PRESSURE_PLATE, 20));
    public static final RegistryObject<Item> GINKGO_SAPLING = REGISTRY.register("ginkgo_sapling", () -> new MoeBlockItem(BlockPartyBlocks.GINKGO_SAPLING, 10));
    public static final RegistryObject<Item> GINKGO_SLAB = REGISTRY.register("ginkgo_slab", () -> new MoeBlockItem(BlockPartyBlocks.GINKGO_SLAB, 20));
    public static final RegistryObject<Item> GINKGO_STAIRS = REGISTRY.register("ginkgo_stairs", () -> new MoeBlockItem(BlockPartyBlocks.GINKGO_STAIRS, 20));
    public static final RegistryObject<Item> GINKGO_WOOD = REGISTRY.register("ginkgo_wood", () -> new MoeBlockItem(BlockPartyBlocks.GINKGO_WOOD, 20));
    public static final RegistryObject<Item> GRAY_PAPER_LANTERN = REGISTRY.register("gray_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.GRAY_PAPER_LANTERN));
    public static final RegistryObject<Item> GREEN_PAPER_LANTERN = REGISTRY.register("green_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.GREEN_PAPER_LANTERN));
    public static final RegistryObject<Item> LETTER = REGISTRY.register("letter", LetterItem::new);
    public static final RegistryObject<Item> LIGHT_BLUE_PAPER_LANTERN = REGISTRY.register("light_blue_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.LIGHT_BLUE_PAPER_LANTERN));
    public static final RegistryObject<Item> LIGHT_GRAY_PAPER_LANTERN = REGISTRY.register("light_gray_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.LIGHT_GRAY_PAPER_LANTERN));
    public static final RegistryObject<Item> LIME_PAPER_LANTERN = REGISTRY.register("lime_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.LIME_PAPER_LANTERN));
    public static final RegistryObject<Item> MAGENTA_PAPER_LANTERN = REGISTRY.register("magenta_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.MAGENTA_PAPER_LANTERN));
    public static final RegistryObject<Item> MIDNIGHT_HANGING_SCROLL = REGISTRY.register("midnight_hanging_scroll", () -> new MoeBlockItem(BlockPartyBlocks.MIDNIGHT_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> MORNING_HANGING_SCROLL = REGISTRY.register("morning_hanging_scroll", () -> new MoeBlockItem(BlockPartyBlocks.MORNING_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> MUSIC_DISC_ANTEATER_SANCTUARY = REGISTRY.register("music_disc_anteater_sanctuary", () -> new MoeMusicItem(BlockPartySounds.MUSIC_DISC_ANTEATER_SANCTUARY));
    public static final RegistryObject<Item> MUSIC_DISC_SAKURA_SAKURA = REGISTRY.register("music_disc_sakura_sakura", () -> new MoeMusicItem(BlockPartySounds.MUSIC_DISC_SAKURA_SAKURA));
    public static final RegistryObject<Item> NIGHT_HANGING_SCROLL = REGISTRY.register("night_hanging_scroll", () -> new MoeBlockItem(BlockPartyBlocks.NIGHT_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> NOON_HANGING_SCROLL = REGISTRY.register("noon_hanging_scroll", () -> new MoeBlockItem(BlockPartyBlocks.NOON_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> ONIGIRI = REGISTRY.register("onigiri", OnigiriItem::new);
    public static final RegistryObject<Item> ORANGE_PAPER_LANTERN = REGISTRY.register("orange_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.ORANGE_PAPER_LANTERN));
    public static final RegistryObject<Item> NPC_SPAWN_EGG = REGISTRY.register("npc_spawn_egg", CustomSpawnEggItem::new);
    public static final RegistryObject<Item> PINK_BOW = REGISTRY.register("pink_bow", PinkBowItem::new);
    public static final RegistryObject<Item> PINK_PAPER_LANTERN = REGISTRY.register("pink_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.PINK_PAPER_LANTERN));
    public static final RegistryObject<Item> PURPLE_PAPER_LANTERN = REGISTRY.register("purple_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.PURPLE_PAPER_LANTERN));
    public static final RegistryObject<Item> RED_PAPER_LANTERN = REGISTRY.register("red_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.RED_PAPER_LANTERN));
    public static final RegistryObject<Item> SAKURA_BLOSSOMS = REGISTRY.register("sakura_blossoms", () -> new MoeBlockItem(BlockPartyBlocks.SAKURA_BLOSSOMS, 10));
    public static final RegistryObject<Item> SAKURA_BUTTON = REGISTRY.register("sakura_button", () -> new MoeBlockItem(BlockPartyBlocks.SAKURA_BUTTON, 20));
    public static final RegistryObject<Item> SAKURA_FENCE = REGISTRY.register("sakura_fence", () -> new MoeBlockItem(BlockPartyBlocks.SAKURA_FENCE, 20));
    public static final RegistryObject<Item> SAKURA_FENCE_GATE = REGISTRY.register("sakura_fence_gate", () -> new MoeBlockItem(BlockPartyBlocks.SAKURA_FENCE_GATE, 20));
    public static final RegistryObject<Item> SAKURA_LOG = REGISTRY.register("sakura_log", () -> new MoeBlockItem(BlockPartyBlocks.SAKURA_LOG, 20));
    public static final RegistryObject<Item> SAKURA_PLANKS = REGISTRY.register("sakura_planks", () -> new MoeBlockItem(BlockPartyBlocks.SAKURA_PLANKS, 20));
    public static final RegistryObject<Item> SAKURA_PRESSURE_PLATE = REGISTRY.register("sakura_pressure_plate", () -> new MoeBlockItem(BlockPartyBlocks.SAKURA_PRESSURE_PLATE, 20));
    public static final RegistryObject<Item> SAKURA_SAPLING = REGISTRY.register("sakura_sapling", () -> new MoeBlockItem(BlockPartyBlocks.SAKURA_SAPLING, 10));
    public static final RegistryObject<Item> SAKURA_SLAB = REGISTRY.register("sakura_slab", () -> new MoeBlockItem(BlockPartyBlocks.SAKURA_SLAB, 20));
    public static final RegistryObject<Item> SAKURA_STAIRS = REGISTRY.register("sakura_stairs", () -> new MoeBlockItem(BlockPartyBlocks.SAKURA_STAIRS, 20));
    public static final RegistryObject<Item> SAKURA_WOOD = REGISTRY.register("sakura_wood", () -> new MoeBlockItem(BlockPartyBlocks.SAKURA_WOOD, 20));
    public static final RegistryObject<Item> SHIMENAWA = REGISTRY.register("shimenawa", () -> new MoeBlockItem(BlockPartyBlocks.SHIMENAWA, 6));
    public static final RegistryObject<Item> SHOJI_BLOCK = REGISTRY.register("shoji_block", () -> new MoeBlockItem(BlockPartyBlocks.SHOJI_BLOCK, 20));
    public static final RegistryObject<Item> SHOJI_LANTERN = REGISTRY.register("shoji_lantern", () -> new MoeBlockItem(BlockPartyBlocks.SHOJI_LANTERN, 20));
    public static final RegistryObject<Item> SHOJI_PANEL = REGISTRY.register("shoji_panel", () -> new MoeBlockItem(BlockPartyBlocks.SHOJI_PANEL, 20));
    public static final RegistryObject<Item> SHOJI_SCREEN = REGISTRY.register("shoji_screen", () -> new MoeBlockItem(BlockPartyBlocks.SHOJI_SCREEN, 20));
    public static final RegistryObject<Item> STRIPPED_GINKGO_LOG = REGISTRY.register("stripped_ginkgo_log", () -> new MoeBlockItem(BlockPartyBlocks.STRIPPED_GINKGO_LOG, 20));
    public static final RegistryObject<Item> STRIPPED_GINKGO_WOOD = REGISTRY.register("stripped_ginkgo_wood", () -> new MoeBlockItem(BlockPartyBlocks.STRIPPED_GINKGO_WOOD, 20));
    public static final RegistryObject<Item> STRIPPED_SAKURA_LOG = REGISTRY.register("stripped_sakura_log", () -> new MoeBlockItem(BlockPartyBlocks.STRIPPED_SAKURA_LOG, 20));
    public static final RegistryObject<Item> STRIPPED_SAKURA_WOOD = REGISTRY.register("stripped_sakura_wood", () -> new MoeBlockItem(BlockPartyBlocks.STRIPPED_SAKURA_WOOD, 20));
    public static final RegistryObject<Item> TATAMI_MAT = REGISTRY.register("tatami_mat", () -> new MoeBlockItem(BlockPartyBlocks.TATAMI_MAT, 20));
    public static final RegistryObject<Item> TORII_TABLET = REGISTRY.register("torii_tablet", () -> new MoeBlockItem(BlockPartyBlocks.TORII_TABLET, 5));
    public static final RegistryObject<Item> WHITE_PAPER_LANTERN = REGISTRY.register("white_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.WHITE_PAPER_LANTERN));
    public static final RegistryObject<Item> WHITE_SAKURA_BLOSSOMS = REGISTRY.register("white_sakura_blossoms", () -> new MoeBlockItem(BlockPartyBlocks.WHITE_SAKURA_BLOSSOMS, 10));
    public static final RegistryObject<Item> WHITE_SAKURA_SAPLING = REGISTRY.register("white_sakura_sapling", () -> new MoeBlockItem(BlockPartyBlocks.WHITE_SAKURA_SAPLING, 10));
    public static final RegistryObject<Item> WISTERIA_BINE = REGISTRY.register("wisteria_bine", () -> new MoeBlockItem(BlockPartyBlocks.WISTERIA_BINE, 20));
    public static final RegistryObject<Item> WISTERIA_LEAVES = REGISTRY.register("wisteria_leaves", () -> new MoeBlockItem(BlockPartyBlocks.WISTERIA_LEAVES, 20));
    public static final RegistryObject<Item> WISTERIA_SAPLING = REGISTRY.register("wisteria_sapling", () -> new MoeBlockItem(BlockPartyBlocks.WISTERIA_SAPLING, 20));
    public static final RegistryObject<Item> WISTERIA_VINES = REGISTRY.register("wisteria_vines", () -> new MoeBlockItem(BlockPartyBlocks.WISTERIA_VINE_TIP, 20));
    public static final RegistryObject<Item> WRITING_TABLE = REGISTRY.register("writing_table", () -> new MoeBlockItem(BlockPartyBlocks.WRITING_TABLE, 5));
    public static final RegistryObject<Item> YEARBOOK = REGISTRY.register("yearbook", YearbookItem::new);
    public static final RegistryObject<Item> YEARBOOK_PAGE = REGISTRY.register("yearbook_page", YearbookPageItem::new);
    public static final RegistryObject<Item> YELLOW_PAPER_LANTERN = REGISTRY.register("yellow_paper_lantern", () -> new MoeBlockItem(BlockPartyBlocks.YELLOW_PAPER_LANTERN));

    public static void registerModelProperties() {
        ItemProperties.register(BlockPartyItems.LETTER.get(), new ResourceLocation("closed"), (stack, world, entity, damage) -> LetterItem.isClosed(stack));
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
