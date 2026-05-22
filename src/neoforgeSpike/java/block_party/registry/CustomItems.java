package block_party.registry;

import block_party.BlockParty;
import block_party.items.CustomSpawnEggItem;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CustomItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BlockParty.ID);
    public static final Map<String, DeferredItem<? extends Item>> ENTRIES = new LinkedHashMap<>();

    public static final DeferredItem<BlockItem> SHOJI_BLOCK = registerBlockItem("shoji_block");
    public static final DeferredItem<Item> MOE_SPAWN_EGG = registerMoeSpawnEgg();
    public static final DeferredItem<Item> CELL_PHONE = registerSimple("cell_phone");
    public static final DeferredItem<Item> YEARBOOK = registerSimple("yearbook");

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
                "yearbook_page",
                "wisteria_vines");
    }

    private CustomItems() {
    }

    private static DeferredItem<BlockItem> registerBlockItem(String id) {
        DeferredItem<BlockItem> item = ITEMS.registerSimpleBlockItem(id, CustomBlocks.ENTRIES.get(id));
        ENTRIES.put(id, item);
        return item;
    }

    private static DeferredItem<Item> registerSimple(String id) {
        DeferredItem<Item> item = ITEMS.registerSimpleItem(id);
        ENTRIES.put(id, item);
        return item;
    }

    private static DeferredItem<Item> registerMoeSpawnEgg() {
        DeferredItem<Item> item = ITEMS.registerItem("moe_spawn_egg", CustomSpawnEggItem::new);
        ENTRIES.put("moe_spawn_egg", item);
        return item;
    }

    private static void registerSimpleItems(String... ids) {
        for (String id : ids) {
            if (!ENTRIES.containsKey(id)) {
                registerSimple(id);
            }
        }
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }
}
