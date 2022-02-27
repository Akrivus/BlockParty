package block_party.registry;

import block_party.BlockParty;
import block_party.items.*;
import block_party.utils.sorters.ISortableItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CustomItems {
    public static final RegistryObject<Item> BENTO_BOX = BlockParty.ITEMS.register("bento_box", BentoBoxItem::new);
    public static final RegistryObject<Item> BLACK_PAPER_LANTERN = BlockParty.ITEMS.register("black_paper_lantern", () -> new MoeBlockItem(CustomBlocks.BLACK_PAPER_LANTERN));
    public static final RegistryObject<Item> BLANK_HANGING_SCROLL = BlockParty.ITEMS.register("blank_hanging_scroll", () -> new MoeBlockItem(CustomBlocks.BLANK_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> BLUE_PAPER_LANTERN = BlockParty.ITEMS.register("blue_paper_lantern", () -> new MoeBlockItem(CustomBlocks.BLUE_PAPER_LANTERN));
    public static final RegistryObject<Item> BROWN_PAPER_LANTERN = BlockParty.ITEMS.register("brown_paper_lantern", () -> new MoeBlockItem(CustomBlocks.BROWN_PAPER_LANTERN));
    public static final RegistryObject<Item> CALLIGRAPHY_BRUSH = BlockParty.ITEMS.register("calligraphy_brush", CalligraphyBrushItem::new);
    public static final RegistryObject<Item> CELL_PHONE = BlockParty.ITEMS.register("cell_phone", CellPhoneItem::new);
    public static final RegistryObject<Item> CUPCAKE = BlockParty.ITEMS.register("cupcake", CupcakeItem::new);
    public static final RegistryObject<Item> CYAN_PAPER_LANTERN = BlockParty.ITEMS.register("cyan_paper_lantern", () -> new MoeBlockItem(CustomBlocks.CYAN_PAPER_LANTERN));
    public static final RegistryObject<Item> DAWN_HANGING_SCROLL = BlockParty.ITEMS.register("dawn_hanging_scroll", () -> new MoeBlockItem(CustomBlocks.DAWN_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> EVENING_HANGING_SCROLL = BlockParty.ITEMS.register("evening_hanging_scroll", () -> new MoeBlockItem(CustomBlocks.EVENING_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> GARDEN_LANTERN = BlockParty.ITEMS.register("garden_lantern", () -> new MoeBlockItem(CustomBlocks.GARDEN_LANTERN, 5));
    public static final RegistryObject<Item> GINKGO_BUTTON = BlockParty.ITEMS.register("ginkgo_button", () -> new MoeBlockItem(CustomBlocks.GINKGO_BUTTON, 20));
    public static final RegistryObject<Item> GINKGO_FENCE = BlockParty.ITEMS.register("ginkgo_fence", () -> new MoeBlockItem(CustomBlocks.GINKGO_FENCE, 20));
    public static final RegistryObject<Item> GINKGO_FENCE_GATE = BlockParty.ITEMS.register("ginkgo_fence_gate", () -> new MoeBlockItem(CustomBlocks.GINKGO_FENCE_GATE, 20));
    public static final RegistryObject<Item> GINKGO_LEAVES = BlockParty.ITEMS.register("ginkgo_leaves", () -> new MoeBlockItem(CustomBlocks.GINKGO_LEAVES, 10));
    public static final RegistryObject<Item> GINKGO_LOG = BlockParty.ITEMS.register("ginkgo_log", () -> new MoeBlockItem(CustomBlocks.GINKGO_LOG, 20));
    public static final RegistryObject<Item> GINKGO_PLANKS = BlockParty.ITEMS.register("ginkgo_planks", () -> new MoeBlockItem(CustomBlocks.GINKGO_PLANKS, 20));
    public static final RegistryObject<Item> GINKGO_PRESSURE_PLATE = BlockParty.ITEMS.register("ginkgo_pressure_plate", () -> new MoeBlockItem(CustomBlocks.GINKGO_PRESSURE_PLATE, 20));
    public static final RegistryObject<Item> GINKGO_SAPLING = BlockParty.ITEMS.register("ginkgo_sapling", () -> new MoeBlockItem(CustomBlocks.GINKGO_SAPLING, 10));
    public static final RegistryObject<Item> GINKGO_SLAB = BlockParty.ITEMS.register("ginkgo_slab", () -> new MoeBlockItem(CustomBlocks.GINKGO_SLAB, 20));
    public static final RegistryObject<Item> GINKGO_STAIRS = BlockParty.ITEMS.register("ginkgo_stairs", () -> new MoeBlockItem(CustomBlocks.GINKGO_STAIRS, 20));
    public static final RegistryObject<Item> GINKGO_WOOD = BlockParty.ITEMS.register("ginkgo_wood", () -> new MoeBlockItem(CustomBlocks.GINKGO_WOOD, 20));
    public static final RegistryObject<Item> GRAY_PAPER_LANTERN = BlockParty.ITEMS.register("gray_paper_lantern", () -> new MoeBlockItem(CustomBlocks.GRAY_PAPER_LANTERN));
    public static final RegistryObject<Item> GREEN_PAPER_LANTERN = BlockParty.ITEMS.register("green_paper_lantern", () -> new MoeBlockItem(CustomBlocks.GREEN_PAPER_LANTERN));
    public static final RegistryObject<Item> LETTER = BlockParty.ITEMS.register("letter", LetterItem::new);
    public static final RegistryObject<Item> LIGHT_BLUE_PAPER_LANTERN = BlockParty.ITEMS.register("light_blue_paper_lantern", () -> new MoeBlockItem(CustomBlocks.LIGHT_BLUE_PAPER_LANTERN));
    public static final RegistryObject<Item> LIGHT_GRAY_PAPER_LANTERN = BlockParty.ITEMS.register("light_gray_paper_lantern", () -> new MoeBlockItem(CustomBlocks.LIGHT_GRAY_PAPER_LANTERN));
    public static final RegistryObject<Item> LIME_PAPER_LANTERN = BlockParty.ITEMS.register("lime_paper_lantern", () -> new MoeBlockItem(CustomBlocks.LIME_PAPER_LANTERN));
    public static final RegistryObject<Item> MAGENTA_PAPER_LANTERN = BlockParty.ITEMS.register("magenta_paper_lantern", () -> new MoeBlockItem(CustomBlocks.MAGENTA_PAPER_LANTERN));
    public static final RegistryObject<Item> MASKED_SAMURAI_KABUTO = BlockParty.ITEMS.register("masked_samurai_kabuto", () -> new MaskedSamuraiItem(EquipmentSlot.HEAD));
    public static final RegistryObject<Item> MIDNIGHT_HANGING_SCROLL = BlockParty.ITEMS.register("midnight_hanging_scroll", () -> new MoeBlockItem(CustomBlocks.MIDNIGHT_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> MORNING_HANGING_SCROLL = BlockParty.ITEMS.register("morning_hanging_scroll", () -> new MoeBlockItem(CustomBlocks.MORNING_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> MUSIC_DISC_ANTEATER_SANCTUARY = BlockParty.ITEMS.register("music_disc_anteater_sanctuary", () -> new MoeMusicItem(CustomSounds.MUSIC_DISC_ANTEATER_SANCTUARY));
    public static final RegistryObject<Item> MUSIC_DISC_SAKURA_SAKURA = BlockParty.ITEMS.register("music_disc_sakura_sakura", () -> new MoeMusicItem(CustomSounds.MUSIC_DISC_SAKURA_SAKURA));
    public static final RegistryObject<Item> NIGHT_HANGING_SCROLL = BlockParty.ITEMS.register("night_hanging_scroll", () -> new MoeBlockItem(CustomBlocks.NIGHT_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> NOON_HANGING_SCROLL = BlockParty.ITEMS.register("noon_hanging_scroll", () -> new MoeBlockItem(CustomBlocks.NOON_HANGING_SCROLL, 90));
    public static final RegistryObject<Item> ONIGIRI = BlockParty.ITEMS.register("onigiri", OnigiriItem::new);
    public static final RegistryObject<Item> ORANGE_PAPER_LANTERN = BlockParty.ITEMS.register("orange_paper_lantern", () -> new MoeBlockItem(CustomBlocks.ORANGE_PAPER_LANTERN));
    public static final RegistryObject<Item> MOE_SPAWN_EGG = BlockParty.ITEMS.register("moe_spawn_egg", CustomSpawnEggItem::new);
    public static final RegistryObject<Item> PINK_BOW = BlockParty.ITEMS.register("pink_bow", PinkBowItem::new);
    public static final RegistryObject<Item> PINK_PAPER_LANTERN = BlockParty.ITEMS.register("pink_paper_lantern", () -> new MoeBlockItem(CustomBlocks.PINK_PAPER_LANTERN));
    public static final RegistryObject<Item> PURPLE_PAPER_LANTERN = BlockParty.ITEMS.register("purple_paper_lantern", () -> new MoeBlockItem(CustomBlocks.PURPLE_PAPER_LANTERN));
    public static final RegistryObject<Item> RED_PAPER_LANTERN = BlockParty.ITEMS.register("red_paper_lantern", () -> new MoeBlockItem(CustomBlocks.RED_PAPER_LANTERN));
    public static final RegistryObject<Item> SAKURA_BLOSSOMS = BlockParty.ITEMS.register("sakura_blossoms", () -> new MoeBlockItem(CustomBlocks.SAKURA_BLOSSOMS, 10));
    public static final RegistryObject<Item> SAKURA_BUTTON = BlockParty.ITEMS.register("sakura_button", () -> new MoeBlockItem(CustomBlocks.SAKURA_BUTTON, 20));
    public static final RegistryObject<Item> SAKURA_FENCE = BlockParty.ITEMS.register("sakura_fence", () -> new MoeBlockItem(CustomBlocks.SAKURA_FENCE, 20));
    public static final RegistryObject<Item> SAKURA_FENCE_GATE = BlockParty.ITEMS.register("sakura_fence_gate", () -> new MoeBlockItem(CustomBlocks.SAKURA_FENCE_GATE, 20));
    public static final RegistryObject<Item> SAKURA_LOG = BlockParty.ITEMS.register("sakura_log", () -> new MoeBlockItem(CustomBlocks.SAKURA_LOG, 20));
    public static final RegistryObject<Item> SAKURA_PLANKS = BlockParty.ITEMS.register("sakura_planks", () -> new MoeBlockItem(CustomBlocks.SAKURA_PLANKS, 20));
    public static final RegistryObject<Item> SAKURA_PRESSURE_PLATE = BlockParty.ITEMS.register("sakura_pressure_plate", () -> new MoeBlockItem(CustomBlocks.SAKURA_PRESSURE_PLATE, 20));
    public static final RegistryObject<Item> SAKURA_SAPLING = BlockParty.ITEMS.register("sakura_sapling", () -> new MoeBlockItem(CustomBlocks.SAKURA_SAPLING, 10));
    public static final RegistryObject<Item> SAKURA_SLAB = BlockParty.ITEMS.register("sakura_slab", () -> new MoeBlockItem(CustomBlocks.SAKURA_SLAB, 20));
    public static final RegistryObject<Item> SAKURA_STAIRS = BlockParty.ITEMS.register("sakura_stairs", () -> new MoeBlockItem(CustomBlocks.SAKURA_STAIRS, 20));
    public static final RegistryObject<Item> SAKURA_WOOD = BlockParty.ITEMS.register("sakura_wood", () -> new MoeBlockItem(CustomBlocks.SAKURA_WOOD, 20));
    public static final RegistryObject<Item> SAMURAI_KABUTO = BlockParty.ITEMS.register("samurai_kabuto", () -> new SamuraiArmorItem(EquipmentSlot.HEAD));
    public static final RegistryObject<Item> SAMURAI_CUIRASS = BlockParty.ITEMS.register("samurai_cuirass", () -> new SamuraiArmorItem(EquipmentSlot.CHEST));
    public static final RegistryObject<Item> SAMURAI_CHAUSSES = BlockParty.ITEMS.register("samurai_chausses", () -> new SamuraiArmorItem(EquipmentSlot.LEGS));
    public static final RegistryObject<Item> SAMURAI_SABATON = BlockParty.ITEMS.register("samurai_sabaton", () -> new SamuraiArmorItem(EquipmentSlot.FEET));
    public static final RegistryObject<Item> SAMURAI_KATANA = BlockParty.ITEMS.register("samurai_katana", () -> new SamuraiKatanaItem());
    public static final RegistryObject<Item> SHIMENAWA = BlockParty.ITEMS.register("shimenawa", () -> new MoeBlockItem(CustomBlocks.SHIMENAWA, 6));
    public static final RegistryObject<Item> SHOJI_BLOCK = BlockParty.ITEMS.register("shoji_block", () -> new MoeBlockItem(CustomBlocks.SHOJI_BLOCK, 20));
    public static final RegistryObject<Item> SHOJI_LANTERN = BlockParty.ITEMS.register("shoji_lantern", () -> new MoeBlockItem(CustomBlocks.SHOJI_LANTERN, 20));
    public static final RegistryObject<Item> SHOJI_PANEL = BlockParty.ITEMS.register("shoji_panel", () -> new MoeBlockItem(CustomBlocks.SHOJI_PANEL, 20));
    public static final RegistryObject<Item> SHOJI_SCREEN = BlockParty.ITEMS.register("shoji_screen", () -> new MoeBlockItem(CustomBlocks.SHOJI_SCREEN, 20));
    public static final RegistryObject<Item> STRIPPED_GINKGO_LOG = BlockParty.ITEMS.register("stripped_ginkgo_log", () -> new MoeBlockItem(CustomBlocks.STRIPPED_GINKGO_LOG, 20));
    public static final RegistryObject<Item> STRIPPED_GINKGO_WOOD = BlockParty.ITEMS.register("stripped_ginkgo_wood", () -> new MoeBlockItem(CustomBlocks.STRIPPED_GINKGO_WOOD, 20));
    public static final RegistryObject<Item> STRIPPED_SAKURA_LOG = BlockParty.ITEMS.register("stripped_sakura_log", () -> new MoeBlockItem(CustomBlocks.STRIPPED_SAKURA_LOG, 20));
    public static final RegistryObject<Item> STRIPPED_SAKURA_WOOD = BlockParty.ITEMS.register("stripped_sakura_wood", () -> new MoeBlockItem(CustomBlocks.STRIPPED_SAKURA_WOOD, 20));
    public static final RegistryObject<Item> TATAMI_MAT = BlockParty.ITEMS.register("tatami_mat", () -> new MoeBlockItem(CustomBlocks.TATAMI_MAT, 20));
    public static final RegistryObject<Item> SHRINE_TABLET = BlockParty.ITEMS.register("shrine_tablet", () -> new MoeBlockItem(CustomBlocks.SHRINE_TABLET, 5));
    public static final RegistryObject<Item> WHITE_PAPER_LANTERN = BlockParty.ITEMS.register("white_paper_lantern", () -> new MoeBlockItem(CustomBlocks.WHITE_PAPER_LANTERN));
    public static final RegistryObject<Item> WHITE_SAKURA_BLOSSOMS = BlockParty.ITEMS.register("white_sakura_blossoms", () -> new MoeBlockItem(CustomBlocks.WHITE_SAKURA_BLOSSOMS, 10));
    public static final RegistryObject<Item> WHITE_SAKURA_SAPLING = BlockParty.ITEMS.register("white_sakura_sapling", () -> new MoeBlockItem(CustomBlocks.WHITE_SAKURA_SAPLING, 10));
    public static final RegistryObject<Item> WISTERIA_BINE = BlockParty.ITEMS.register("wisteria_bine", () -> new MoeBlockItem(CustomBlocks.WISTERIA_BINE, 20));
    public static final RegistryObject<Item> WISTERIA_LEAVES = BlockParty.ITEMS.register("wisteria_leaves", () -> new MoeBlockItem(CustomBlocks.WISTERIA_LEAVES, 20));
    public static final RegistryObject<Item> WISTERIA_SAPLING = BlockParty.ITEMS.register("wisteria_sapling", () -> new MoeBlockItem(CustomBlocks.WISTERIA_SAPLING, 20));
    public static final RegistryObject<Item> WISTERIA_VINES = BlockParty.ITEMS.register("wisteria_vines", () -> new MoeBlockItem(CustomBlocks.WISTERIA_VINE_TIP, 20));
    public static final RegistryObject<Item> WOODEN_BOKKEN = BlockParty.ITEMS.register("wooden_bokken", () -> new BokkenItem());
    public static final RegistryObject<Item> WRITING_TABLE = BlockParty.ITEMS.register("writing_table", () -> new MoeBlockItem(CustomBlocks.WRITING_TABLE, 5));
    public static final RegistryObject<Item> YEARBOOK = BlockParty.ITEMS.register("yearbook", YearbookItem::new);
    public static final RegistryObject<Item> YEARBOOK_PAGE = BlockParty.ITEMS.register("yearbook_page", YearbookPageItem::new);
    public static final RegistryObject<Item> YELLOW_PAPER_LANTERN = BlockParty.ITEMS.register("yellow_paper_lantern", () -> new MoeBlockItem(CustomBlocks.YELLOW_PAPER_LANTERN));

    public static void add(DeferredRegister<Item> registry, IEventBus bus) {
        bus.addListener(CustomItems::registerModelProperties);
        registry.register(bus);
    }

    private static void registerModelProperties(FMLCommonSetupEvent e) {
        ItemProperties.register(CustomItems.LETTER.get(), new ResourceLocation("closed"), (stack, world, entity, damage) -> LetterItem.isClosed(stack));
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
