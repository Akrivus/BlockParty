package block_party.scene;

import block_party.BlockParty;
import block_party.entities.Moe;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class SceneObservationFactories {
    private static final SceneObservation FAIL_CLOSED = moe -> false;

    private SceneObservationFactories() {
    }

    public static SceneObservation build(ResourceLocation type, JsonObject json) {
        SceneObservation simple = SceneObservations.byPath(type.getPath()).orElse(null);
        if (simple != null) {
            return simple;
        }
        return switch (type.getPath()) {
            case "if_time" -> moe -> compare((int) (moe.level().getDayTime() % 24000L), json);
            case "self" -> moe -> entityMatches(moe, json);
            case "health" -> moe -> compare(moe.getHealth(), json);
            case "food_level" -> moe -> compare(moe.getFoodLevel(), json);
            case "loyalty" -> moe -> compare(moe.getLoyalty(), json);
            case "stress" -> moe -> compare(moe.getStress(), json);
            case "counter" -> moe -> counterMatches(moe, json);
            case "has_cookie" -> moe -> cookieMatches(moe, json);
            case "held_item" -> moe -> itemMatches(moe.getItemInHand(hand(json)), json);
            case "player_held_item" -> moe -> targetPlayer(moe) != null && itemMatches(targetPlayer(moe).getItemInHand(hand(json)), json);
            case "block" -> moe -> blockMatches(moe.getVisibleBlockState(), json);
            case "name" -> moe -> stringMatches(moe.getGivenName(), json);
            case "player_counter", "player_has_cookie", "family_name" -> FAIL_CLOSED;
            default -> FAIL_CLOSED;
        };
    }

    private static boolean counterMatches(Moe moe, JsonObject json) {
        Integer value = SceneVariables.get(moe.level())
                .counters(moe.getDatabaseID())
                .get(GsonHelper.getAsString(json, "name", ""));
        return compare(value == null ? 0 : value, json);
    }

    private static boolean cookieMatches(Moe moe, JsonObject json) {
        String value = SceneVariables.get(moe.level())
                .cookies(moe.getDatabaseID())
                .get(GsonHelper.getAsString(json, "name", ""));
        if (!json.has("value")) {
            return value != null;
        }
        return stringMatches(value, json);
    }

    private static boolean entityMatches(Moe moe, JsonObject json) {
        String name = GsonHelper.getAsString(json, "name", BlockParty.source("moe").toString());
        boolean pass;
        if (name.startsWith("#")) {
            TagKey<EntityType<?>> tag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse(name.substring(1)));
            pass = entityHolder(moe).is(tag);
        } else {
            pass = BuiltInRegistries.ENTITY_TYPE.getKey(moe.getType()).equals(ResourceLocation.parse(name));
        }
        return maybeNegate(pass, json);
    }

    private static Holder.Reference<EntityType<?>> entityHolder(Moe moe) {
        return BuiltInRegistries.ENTITY_TYPE
                .get(ResourceKey.create(Registries.ENTITY_TYPE, BuiltInRegistries.ENTITY_TYPE.getKey(moe.getType())))
                .orElseThrow();
    }

    private static boolean blockMatches(BlockState state, JsonObject json) {
        String name = GsonHelper.getAsString(json, "name", "");
        if (name.isBlank()) {
            return false;
        }
        boolean pass;
        if (name.startsWith("#")) {
            pass = state.is(TagKey.create(Registries.BLOCK, ResourceLocation.parse(name.substring(1))));
        } else {
            pass = state.is(registryValue(BuiltInRegistries.BLOCK, Registries.BLOCK, ResourceLocation.parse(name)));
        }
        return maybeNegate(pass, json);
    }

    private static boolean itemMatches(ItemStack stack, JsonObject json) {
        String name = GsonHelper.getAsString(json, "name", "");
        if (name.isBlank()) {
            return false;
        }
        boolean pass;
        if (name.startsWith("#")) {
            pass = stack.is(TagKey.create(Registries.ITEM, ResourceLocation.parse(name.substring(1))));
        } else {
            pass = stack.is(registryValue(BuiltInRegistries.ITEM, Registries.ITEM, ResourceLocation.parse(name)));
        }
        if (pass && json.has("count")) {
            pass = compare(stack.getCount(), json.getAsJsonObject("count"));
        }
        return maybeNegate(pass, json);
    }

    private static <T> T registryValue(Registry<T> registry, ResourceKey<? extends Registry<T>> registryKey, ResourceLocation id) {
        return registry.get(ResourceKey.create(registryKey, id)).orElseThrow().value();
    }

    private static boolean stringMatches(String actual, JsonObject json) {
        if (actual == null) {
            actual = "";
        }
        String expected = GsonHelper.getAsString(json, "value", "");
        boolean pass = switch (GsonHelper.getAsString(json, "operation", "equals")) {
            case "prefix" -> actual.startsWith(expected);
            case "suffix" -> actual.endsWith(expected);
            case "contains" -> actual.contains(expected);
            case "matches" -> actual.matches(expected);
            case "not_equals" -> !actual.equals(expected);
            default -> actual.equals(expected);
        };
        return maybeNegate(pass, json);
    }

    private static boolean compare(float actual, JsonObject json) {
        float expected = GsonHelper.getAsFloat(json, "value", 0.0F);
        boolean pass = switch (GsonHelper.getAsString(json, "operation", "equals")) {
            case "greater_than" -> actual > expected;
            case "greater_than_equals", "at_least" -> actual >= expected;
            case "less_than" -> actual < expected;
            case "less_than_equals", "at_most" -> actual <= expected;
            default -> actual == expected;
        };
        return maybeNegate(pass, json);
    }

    private static InteractionHand hand(JsonObject json) {
        String value = GsonHelper.getAsString(json, "hand", "main_hand").toUpperCase(java.util.Locale.ROOT);
        return InteractionHand.valueOf(value);
    }

    private static ServerPlayer targetPlayer(Moe moe) {
        if (!(moe.level() instanceof net.minecraft.server.level.ServerLevel level)) {
            return null;
        }
        ServerPlayer target = level.getServer().getPlayerList().getPlayer(moe.getDialogueTarget());
        return target == null ? level.getServer().getPlayerList().getPlayer(moe.getPlayerUUID()) : target;
    }

    private static boolean maybeNegate(boolean pass, JsonObject json) {
        return GsonHelper.getAsBoolean(json, "not", false) ? !pass : pass;
    }
}
