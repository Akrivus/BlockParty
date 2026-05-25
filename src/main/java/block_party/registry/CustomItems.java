package block_party.registry;

import block_party.BlockParty;
import block_party.items.CellPhoneItem;
import block_party.items.CustomSpawnEggItem;
import block_party.items.BokkenItem;
import block_party.items.InviteItem;
import block_party.items.LetterItem;
import block_party.items.MaskedSamuraiItem;
import block_party.items.MoeBlockItem;
import block_party.items.MoeMusicItem;
import block_party.items.SamuraiArmorItem;
import block_party.items.SamuraiKatanaItem;
import block_party.items.SimpleSortableItem;
import block_party.items.YearbookItem;
import block_party.items.YearbookPageItem;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BlockParty.ID);
    public static final Map<String, DeferredItem<? extends Item>> ENTRIES = new LinkedHashMap<>();

    public static final DeferredItem<BlockItem> SHOJI_BLOCK = registerBlockItem("shoji_block");
    public static final DeferredItem<Item> MOE_SPAWN_EGG = registerMoeSpawnEgg();
    public static final DeferredItem<Item> CELL_PHONE = registerCellPhone();
    public static final DeferredItem<Item> YEARBOOK = registerYearbook();

    static {
        for (String blockId : CustomBlocks.ENTRIES.keySet()) {
            if (!ENTRIES.containsKey(blockId)) {
                registerBlockItem(blockId);
            }
        }
        registerSimpleItems(
                "bento_box",
                "calligraphy_brush",
                "cupcake",
                "invite",
                "letter",
                "masked_samurai_kabuto",
                "music_disc_anteater_sanctuary",
                "music_disc_sakura_sakura",
                "onigiri",
                "pink_bow",
                "samurai_kabuto",
                "samurai_cuirass",
                "samurai_chausses",
                "samurai_sabaton",
                "samurai_katana",
                "wooden_bokken",
                "wisteria_vines");
        if (!ENTRIES.containsKey("yearbook_page")) {
            registerYearbookPage();
        }
    }

    private CustomItems() {
    }

    private static DeferredItem<BlockItem> registerBlockItem(String id) {
        DeferredItem<BlockItem> item = ITEMS.registerItem(id,
                properties -> new MoeBlockItem(CustomBlocks.ENTRIES.get(id).get(), properties, sortOrderForBlock(id)));
        ENTRIES.put(id, item);
        return item;
    }

    private static DeferredItem<Item> registerSimple(String id) {
        DeferredItem<Item> item = switch (id) {
            case "bento_box" -> ITEMS.registerItem(id, properties -> new SimpleSortableItem(properties.food(food(11, 1.9F)), 10));
            case "calligraphy_brush" -> ITEMS.registerItem(id, properties -> new SimpleSortableItem(properties.stacksTo(1).durability(64), 5));
            case "cupcake" -> ITEMS.registerItem(id, properties -> new SimpleSortableItem(properties.food(food(2, 0.1F)), 10));
            case "invite" -> ITEMS.registerItem(id, InviteItem::new);
            case "letter" -> ITEMS.registerItem(id, LetterItem::new);
            case "onigiri" -> ITEMS.registerItem(id, properties -> new SimpleSortableItem(properties.food(food(2, 0.5F)), 10));
            case "pink_bow" -> ITEMS.registerItem(id, properties -> new SimpleSortableItem(properties, 10));
            case "masked_samurai_kabuto" -> ITEMS.registerItem(id, MaskedSamuraiItem::new);
            case "samurai_kabuto" -> ITEMS.registerItem(id, properties -> new SamuraiArmorItem(properties, ArmorType.HELMET));
            case "samurai_cuirass" -> ITEMS.registerItem(id, properties -> new SamuraiArmorItem(properties, ArmorType.CHESTPLATE));
            case "samurai_chausses" -> ITEMS.registerItem(id, properties -> new SamuraiArmorItem(properties, ArmorType.LEGGINGS));
            case "samurai_sabaton" -> ITEMS.registerItem(id, properties -> new SamuraiArmorItem(properties, ArmorType.BOOTS));
            case "samurai_katana" -> ITEMS.registerItem(id, SamuraiKatanaItem::new);
            case "wooden_bokken" -> ITEMS.registerItem(id, BokkenItem::new);
            case "music_disc_anteater_sanctuary" -> ITEMS.registerItem(id, properties -> new MoeMusicItem(properties, "music_disc.anteater_sanctuary"));
            case "music_disc_sakura_sakura" -> ITEMS.registerItem(id, properties -> new MoeMusicItem(properties, "music_disc.sakura_sakura"));
            case "wisteria_vines" -> ITEMS.registerItem(id, properties -> new SimpleSortableItem(properties, 20));
            default -> ITEMS.registerItem(id, properties -> new SimpleSortableItem(properties, 100));
        };
        ENTRIES.put(id, item);
        return item;
    }

    private static DeferredItem<Item> registerMoeSpawnEgg() {
        DeferredItem<Item> item = ITEMS.registerItem("moe_spawn_egg", CustomSpawnEggItem::new);
        ENTRIES.put("moe_spawn_egg", item);
        return item;
    }

    private static DeferredItem<Item> registerCellPhone() {
        DeferredItem<Item> item = ITEMS.registerItem("cell_phone", CellPhoneItem::new);
        ENTRIES.put("cell_phone", item);
        return item;
    }

    private static DeferredItem<Item> registerYearbook() {
        DeferredItem<Item> item = ITEMS.registerItem("yearbook", YearbookItem::new);
        ENTRIES.put("yearbook", item);
        return item;
    }

    private static DeferredItem<Item> registerYearbookPage() {
        DeferredItem<Item> item = ITEMS.registerItem("yearbook_page", YearbookPageItem::new);
        ENTRIES.put("yearbook_page", item);
        return item;
    }

    private static void registerSimpleItems(String... ids) {
        for (String id : ids) {
            if (!ENTRIES.containsKey(id)) {
                registerSimple(id);
            }
        }
    }

    private static FoodProperties food(int nutrition, float saturation) {
        return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
    }

    private static int sortOrderForBlock(String id) {
        if (id.endsWith("_hanging_scroll")) {
            return 90;
        }
        return switch (id) {
            case "garden_lantern", "shrine_tablet", "writing_table" -> 5;
            case "shimenawa" -> 6;
            case "ginkgo_leaves", "ginkgo_sapling", "sakura_blossoms", "sakura_sapling", "white_sakura_blossoms", "white_sakura_sapling" -> 10;
            case "ginkgo_button", "ginkgo_fence", "ginkgo_fence_gate", "ginkgo_log", "ginkgo_planks",
                    "ginkgo_pressure_plate", "ginkgo_slab", "ginkgo_stairs", "ginkgo_wood",
                    "sakura_button", "sakura_fence", "sakura_fence_gate", "sakura_log", "sakura_planks",
                    "sakura_pressure_plate", "sakura_slab", "sakura_stairs", "sakura_wood",
                    "shoji_block", "shoji_lantern", "shoji_panel", "shoji_screen", "stripped_ginkgo_log",
                    "stripped_ginkgo_wood", "stripped_sakura_log", "stripped_sakura_wood", "tatami_mat",
                    "wisteria_bine", "wisteria_button", "wisteria_fence", "wisteria_fence_gate",
                    "wisteria_leaves", "wisteria_planks", "wisteria_pressure_plate", "wisteria_sapling",
                    "wisteria_slab", "wisteria_stairs" -> 20;
            default -> 100;
        };
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
