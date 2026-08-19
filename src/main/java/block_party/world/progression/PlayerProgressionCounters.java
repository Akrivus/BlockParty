package block_party.world.progression;

import block_party.registry.CustomTags;
import block_party.scene.SceneVariables;
import java.util.UUID;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/** A per-player ledger of tracked item pickups and block breaks, keyed by registry id. */
public final class PlayerProgressionCounters {
    private static final String ITEM_PREFIX = "progression/items/";
    private static final String BLOCK_PREFIX = "progression/blocks/";

    private PlayerProgressionCounters() {
    }

    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack original = event.getOriginalStack();
        if (!original.is(CustomTags.Items.PROGRESSION_COUNTER_ITEMS)) {
            return;
        }
        ItemStack remaining = event.getCurrentStack();
        int amount = original.getCount() - (remaining.is(original.getItem()) ? remaining.getCount() : 0);
        add(player.serverLevel(), player.getUUID(), itemKey(original.getItem()), amount);
    }

    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || !(event.getPlayer() instanceof ServerPlayer player)
                || !event.getState().is(CustomTags.PROGRESSION_COUNTER_BLOCKS)) {
            return;
        }
        add(level, player.getUUID(), blockKey(event.getState().getBlock()), 1);
    }

    public static boolean recordItem(ServerLevel level, UUID player, ItemStack stack, int amount) {
        if (stack == null || stack.isEmpty() || !stack.is(CustomTags.Items.PROGRESSION_COUNTER_ITEMS)) {
            return false;
        }
        return add(level, player, itemKey(stack.getItem()), amount);
    }

    public static int countItems(ServerLevel level, UUID player, TagKey<Item> tag) {
        if (level == null || player == null || tag == null) {
            return 0;
        }
        var counters = SceneVariables.get(level).playerCounters(player);
        long total = 0;
        for (Item item : BuiltInRegistries.ITEM) {
            if (item.builtInRegistryHolder().is(tag)) {
                Integer count = counters.get(itemKey(item));
                total += count == null ? 0 : count;
            }
        }
        return (int) Math.min(Integer.MAX_VALUE, total);
    }

    public static int countItem(ServerLevel level, UUID player, Item item) {
        if (level == null || player == null || item == null) {
            return 0;
        }
        Integer count = SceneVariables.get(level).playerCounters(player).get(itemKey(item));
        return count == null ? 0 : count;
    }

    public static int countBlock(ServerLevel level, UUID player, Block block) {
        if (level == null || player == null || block == null) {
            return 0;
        }
        Integer count = SceneVariables.get(level).playerCounters(player).get(blockKey(block));
        return count == null ? 0 : count;
    }

    public static int countBlocks(ServerLevel level, UUID player, TagKey<Block> tag) {
        if (level == null || player == null || tag == null) {
            return 0;
        }
        var counters = SceneVariables.get(level).playerCounters(player);
        long total = 0;
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block.builtInRegistryHolder().is(tag)) {
                Integer count = counters.get(blockKey(block));
                total += count == null ? 0 : count;
            }
        }
        return (int) Math.min(Integer.MAX_VALUE, total);
    }

    public static void resetItems(ServerLevel level, UUID player, TagKey<Item> tag) {
        if (level == null || player == null || tag == null) {
            return;
        }
        var counters = SceneVariables.get(level).playerCounters(player);
        for (Item item : BuiltInRegistries.ITEM) {
            if (item.builtInRegistryHolder().is(tag)) {
                counters.delete(itemKey(item));
            }
        }
    }

    public static void resetBlocks(ServerLevel level, UUID player, TagKey<Block> tag) {
        if (level == null || player == null || tag == null) {
            return;
        }
        var counters = SceneVariables.get(level).playerCounters(player);
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block.builtInRegistryHolder().is(tag)) {
                counters.delete(blockKey(block));
            }
        }
    }

    public static void resetItem(ServerLevel level, UUID player, Item item) {
        if (level != null && player != null && item != null) {
            SceneVariables.get(level).playerCounters(player).delete(itemKey(item));
        }
    }

    public static void resetBlock(ServerLevel level, UUID player, Block block) {
        if (level != null && player != null && block != null) {
            SceneVariables.get(level).playerCounters(player).delete(blockKey(block));
        }
    }

    private static boolean add(ServerLevel level, UUID player, String key, int amount) {
        if (level == null || player == null || amount <= 0) {
            return false;
        }
        var counters = SceneVariables.get(level).playerCounters(player);
        Integer stored = counters.get(key);
        int current = stored == null ? 0 : stored;
        counters.set(key, (int) Math.min(Integer.MAX_VALUE, (long) current + amount));
        return true;
    }

    private static String itemKey(Item item) {
        return ITEM_PREFIX + BuiltInRegistries.ITEM.getKey(item);
    }

    private static String blockKey(Block block) {
        return BLOCK_PREFIX + BuiltInRegistries.BLOCK.getKey(block);
    }
}
