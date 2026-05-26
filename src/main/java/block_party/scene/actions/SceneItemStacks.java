package block_party.scene.actions;

import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class SceneItemStacks {
    private SceneItemStacks() {
    }

    public static ItemStack parse(JsonObject json) {
        String name = GsonHelper.getAsString(json, json.has("item") ? "item" : "name", "");
        if (name.isBlank() || name.startsWith("#")) {
            return ItemStack.EMPTY;
        }
        ResourceLocation id = ResourceLocation.tryParse(name);
        if (id == null) {
            return ItemStack.EMPTY;
        }
        Item item = BuiltInRegistries.ITEM
                .get(ResourceKey.create(Registries.ITEM, id))
                .map(Holder.Reference::value)
                .orElse(null);
        if (item == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item, Math.max(1, GsonHelper.getAsInt(json, "count", 1)));
    }

    public static boolean matches(ItemStack stack, JsonObject json) {
        String name = GsonHelper.getAsString(json, json.has("item") ? "item" : "name", "");
        if (name.isBlank()) {
            return false;
        }
        boolean pass;
        if (name.startsWith("#")) {
            pass = stack.is(TagKey.create(Registries.ITEM, ResourceLocation.parse(name.substring(1))));
        } else {
            ResourceLocation id = ResourceLocation.parse(name);
            Item item = BuiltInRegistries.ITEM
                    .get(ResourceKey.create(Registries.ITEM, id))
                    .map(Holder.Reference::value)
                    .orElse(null);
            pass = item != null && stack.is(item);
        }
        return pass;
    }

    public static int count(Container inventory, JsonObject json) {
        int count = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); ++slot) {
            ItemStack stack = inventory.getItem(slot);
            if (matches(stack, json)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static boolean has(Container inventory, JsonObject json) {
        return count(inventory, json) >= Math.max(1, GsonHelper.getAsInt(json, "count", 1));
    }

    public static ItemStack firstMatch(Container inventory, JsonObject json) {
        for (int slot = 0; slot < inventory.getContainerSize(); ++slot) {
            ItemStack stack = inventory.getItem(slot);
            if (matches(stack, json)) {
                return stack.copyWithCount(Math.max(1, GsonHelper.getAsInt(json, "count", 1)));
            }
        }
        return ItemStack.EMPTY;
    }

    public static int freeSpace(Container inventory, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        int space = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); ++slot) {
            ItemStack existing = inventory.getItem(slot);
            if (existing.isEmpty()) {
                space += stack.getMaxStackSize();
            } else if (ItemStack.isSameItemSameComponents(existing, stack)) {
                space += Math.max(0, existing.getMaxStackSize() - existing.getCount());
            }
        }
        return space;
    }

    public static int insert(Container inventory, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        ItemStack remaining = stack.copy();
        for (int slot = 0; slot < inventory.getContainerSize() && !remaining.isEmpty(); ++slot) {
            ItemStack existing = inventory.getItem(slot);
            if (!existing.isEmpty() && ItemStack.isSameItemSameComponents(existing, remaining)) {
                int moved = Math.min(remaining.getCount(), existing.getMaxStackSize() - existing.getCount());
                if (moved > 0) {
                    existing.grow(moved);
                    remaining.shrink(moved);
                    inventory.setChanged();
                }
            }
        }
        for (int slot = 0; slot < inventory.getContainerSize() && !remaining.isEmpty(); ++slot) {
            if (inventory.getItem(slot).isEmpty()) {
                int moved = Math.min(remaining.getCount(), remaining.getMaxStackSize());
                ItemStack inserted = remaining.copyWithCount(moved);
                inventory.setItem(slot, inserted);
                remaining.shrink(moved);
            }
        }
        return stack.getCount() - remaining.getCount();
    }

    public static ItemStack remove(Container inventory, JsonObject json, int count) {
        ItemStack removed = ItemStack.EMPTY;
        int remaining = Math.max(0, count);
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; ++slot) {
            ItemStack stack = inventory.getItem(slot);
            if (!matches(stack, json)) {
                continue;
            }
            if (!removed.isEmpty() && !ItemStack.isSameItemSameComponents(removed, stack)) {
                continue;
            }
            int moved = Math.min(remaining, stack.getCount());
            ItemStack part = stack.copyWithCount(moved);
            if (removed.isEmpty()) {
                removed = part;
            } else {
                removed.grow(moved);
            }
            stack.shrink(moved);
            if (stack.isEmpty()) {
                inventory.setItem(slot, ItemStack.EMPTY);
            } else {
                inventory.setChanged();
            }
            remaining -= moved;
        }
        return removed;
    }
}
