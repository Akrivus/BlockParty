package block_party.scene;

import block_party.BlockParty;
import block_party.db.BlockPartyDB;
import block_party.db.records.AttentionRecord;
import block_party.db.records.PlayerRelationship;
import block_party.entities.Moe;
import block_party.entities.environment.MoePlaceMemory;
import block_party.entities.movement.MoeAnchor;
import block_party.entities.preferences.MoeItemPreferences;
import block_party.entities.social.MoeSocialContext;
import block_party.entities.social.SocialAffinities;
import block_party.scene.actions.SceneItemStacks;
import com.google.gson.JsonObject;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
            case "target_affection" -> moe -> targetRelationship(moe).map(relationship -> compare(relationship.affection(), json)).orElse(false);
            case "target_loyalty" -> moe -> targetRelationship(moe).map(relationship -> compare(relationship.loyalty(), json)).orElse(false);
            case "target_trust" -> moe -> targetRelationship(moe).map(relationship -> compare(relationship.trust(), json)).orElse(false);
            case "target_relationship_stress" -> moe -> targetRelationship(moe).map(relationship -> compare(relationship.stress(), json)).orElse(false);
            case "target_yearbook_signed" -> moe -> targetRelationship(moe).map(PlayerRelationship::yearbookSigned).map(pass -> maybeNegate(pass, json)).orElse(false);
            case "target_phone_contact" -> moe -> targetRelationship(moe).map(PlayerRelationship::phoneContact).map(pass -> maybeNegate(pass, json)).orElse(false);
            case "has_attention" -> moe -> maybeNegate(attention(moe, json).isPresent(), json);
            case "attention_type" -> moe -> attention(moe, json).map(record -> attentionKeyMatches(record.type(), json, "type")).orElse(false);
            case "attention_source" -> moe -> attention(moe, json).map(record -> attentionKeyMatches(record.source(), json, "source")).orElse(false);
            case "attention_item" -> moe -> attention(moe, json).map(record -> itemMatches(new ItemStack(registryValue(BuiltInRegistries.ITEM, Registries.ITEM, ResourceLocation.parse(record.itemId())), Math.max(1, record.itemCount())), json)).orElse(false);
            case "attention_count" -> moe -> attention(moe, json).map(record -> compare(record.count(), json)).orElse(false);
            case "attention_block" -> moe -> attention(moe, json).map(record -> blockMatches(record.blockState(), json)).orElse(false);
            case "follow_intent" -> moe -> enumMatches(moe.getFollowIntent(), json);
            case "follow_ticks_remaining" -> moe -> compare(moe.getFollowTicksRemaining(), json);
            case "follow_player_is_target" -> moe -> maybeNegate(moe.getFollowPlayerUUID().equals(targetPlayerUuid(moe)), json);
            case "has_anchor" -> moe -> maybeNegate(anchorMatches(moe, json), json);
            case "anchor_type" -> moe -> currentAnchor(moe).map(anchor -> enumMatches(anchor.type(), json)).orElse(false);
            case "anchor_distance" -> moe -> currentAnchor(moe).map(anchor -> compare(anchorDistance(moe, anchor), json)).orElse(false);
            case "anchor_priority" -> moe -> currentAnchor(moe).map(anchor -> compare(anchor.priority(), json)).orElse(false);
            case "anchor_player_owned" -> moe -> currentAnchor(moe)
                    .map(anchor -> maybeNegate(anchor.playerUuid() != null && anchor.playerUuid().equals(moe.getPlayerUUID()), json))
                    .orElse(false);
            case "routine_intent" -> moe -> enumMatches(moe.getEffectiveRoutineIntent(), json);
            case "explicit_routine_intent" -> moe -> enumMatches(moe.getRoutineIntent(), json);
            case "counter" -> moe -> counterMatches(moe, json);
            case "has_cookie" -> moe -> cookieMatches(moe, json);
            case "held_item" -> moe -> itemMatches(moe.getItemInHand(hand(json)), json);
            case "player_held_item" -> moe -> targetPlayer(moe) != null && itemMatches(targetPlayer(moe).getItemInHand(hand(json)), json);
            case "has_item", "moe_has_item" -> moe -> maybeNegate(SceneItemStacks.has(moe.getInventory(), json), json);
            case "player_has_item" -> moe -> targetPlayer(moe) != null && maybeNegate(SceneItemStacks.has(targetPlayer(moe).getInventory(), json), json);
            case "block" -> moe -> blockMatches(moe.getVisibleBlockState(), json);
            case "name" -> moe -> stringMatches(moe.getGivenName(), json);
            case "has_social_target" -> moe -> MoeSocialContext.find(moe, socialRadius(json)).isPresent();
            case "social_affinity" -> moe -> socialContext(moe, json).map(context -> compare(context.signal().affinity(), json)).orElse(false);
            case "social_tension" -> moe -> socialContext(moe, json).map(context -> compare(context.signal().tension(), json)).orElse(false);
            case "social_interest" -> moe -> socialContext(moe, json).map(context -> compare(context.signal().interest(), json)).orElse(false);
            case "social_visual" -> moe -> socialContext(moe, json).map(context -> enumMatches(context.visual(), json)).orElse(false);
            case "social_reaction" -> moe -> socialContext(moe, json).map(context -> enumMatches(context.reaction(), json)).orElse(false);
            case "social_target_name" -> moe -> socialContext(moe, json).map(context -> stringMatches(context.target().getGivenName(), json)).orElse(false);
            case "social_target_block" -> moe -> socialContext(moe, json).map(context -> blockMatches(context.target().getVisibleBlockState(), json)).orElse(false);
            case "social_target_blood_type" -> moe -> socialContext(moe, json).map(context -> traitMatches(context.target().getBloodType(), json)).orElse(false);
            case "social_target_dere" -> moe -> socialContext(moe, json).map(context -> traitMatches(context.target().getDere(), json)).orElse(false);
            case "social_target_zodiac" -> moe -> socialContext(moe, json).map(context -> traitMatches(context.target().getZodiac(), json)).orElse(false);
            case "social_target_emotion" -> moe -> socialContext(moe, json).map(context -> traitMatches(context.target().getEmotion(), json)).orElse(false);
            case "remembered_place_type" -> moe -> moe.rememberedPlace().map(place -> enumMatches(place.type(), json)).orElse(false);
            case "remembered_place_score" -> moe -> moe.rememberedPlace().map(place -> compare((float) place.score(), json)).orElse(false);
            case "remembered_place_occupancy" -> moe -> moe.rememberedPlace().map(place -> compare(place.occupancy(), json)).orElse(false);
            case "remembered_place_capacity" -> moe -> moe.rememberedPlace().map(place -> compare(place.capacity(), json)).orElse(false);
            case "remembered_place_anchor_type" -> moe -> moe.rememberedPlace().map(place -> place.features().anchorType() != null && enumMatches(place.features().anchorType(), json)).orElse(false);
            case "remembered_place_has_garden_lantern" -> moe -> moe.rememberedPlace()
                    .map(place -> maybeNegate(MoePlaceMemory.hasGardenLantern(moe, place), json))
                    .orElse(false);
            case "remembered_place_has_lit_garden_lantern" -> moe -> moe.rememberedPlace()
                    .map(place -> maybeNegate(MoePlaceMemory.hasLitGardenLantern(moe, place), json))
                    .orElse(false);
            case "remembered_place_has_unlit_garden_lantern" -> moe -> moe.rememberedPlace()
                    .map(place -> maybeNegate(MoePlaceMemory.hasUnlitGardenLantern(moe, place), json))
                    .orElse(false);
            case "observed_block" -> moe -> moe.latestEnvironmentalObservation().map(observation -> blockMatches(observation.state(), json)).orElse(false);
            case "observed_signal_layer" -> moe -> moe.latestEnvironmentalObservation().map(observation -> enumMatches(observation.layeredSignal().strongestLayer(), json)).orElse(false);
            case "observed_affinity" -> moe -> moe.latestEnvironmentalObservation().map(observation -> compare(observation.signal().affinity(), json)).orElse(false);
            case "observed_tension" -> moe -> moe.latestEnvironmentalObservation().map(observation -> compare(observation.signal().tension(), json)).orElse(false);
            case "observed_interest" -> moe -> moe.latestEnvironmentalObservation().map(observation -> compare(observation.signal().interest(), json)).orElse(false);
            case "gift_preference" -> moe -> moe.latestGiftPreferenceSignal().map(signal -> compare(signal.preference(), json)).orElse(false);
            case "gift_aversion" -> moe -> moe.latestGiftPreferenceSignal().map(signal -> compare(signal.aversion(), json)).orElse(false);
            case "gift_interest" -> moe -> moe.latestGiftPreferenceSignal().map(signal -> compare(signal.interest(), json)).orElse(false);
            case "gift_begging" -> moe -> moe.latestGiftPreferenceSignal().map(signal -> compare(signal.begging(), json)).orElse(false);
            case "gift_item" -> moe -> moe.latestGiftItem().map(stack -> itemMatches(stack, json)).orElse(false);
            case "held_item_preference" -> moe -> targetPlayer(moe) != null && compare(heldItemSignal(moe, json).preference(), json);
            case "held_item_begging" -> moe -> targetPlayer(moe) != null && compare(heldItemSignal(moe, json).begging(), json);
            case "social_place_behavior" -> moe -> moe.socialPlaceMemoryForTests().map(memory -> enumMatches(memory.behavior(), json)).orElse(false);
            case "social_place_type" -> moe -> moe.socialPlaceMemoryForTests().map(memory -> enumMatches(memory.type(), json)).orElse(false);
            case "social_place_distance" -> moe -> moe.socialPlaceMemoryForTests().map(memory -> compare((float) Math.sqrt(memory.pos().distSqr(moe.blockPosition())), json)).orElse(false);
            case "social_place_owner_name" -> moe -> moe.socialPlaceMemoryForTests().map(memory -> stringMatches(memory.ownerName(), json)).orElse(false);
            case "player_counter", "player_has_cookie", "family_name" -> FAIL_CLOSED;
            default -> FAIL_CLOSED;
        };
    }

    private static Optional<MoeAnchor> currentAnchor(Moe moe) {
        return moe.currentRoutineAnchor();
    }

    private static boolean anchorMatches(Moe moe, JsonObject json) {
        Optional<MoeAnchor> anchor = currentAnchor(moe);
        if (anchor.isEmpty()) {
            return false;
        }
        if (!json.has("type")) {
            return true;
        }
        String expected = GsonHelper.getAsString(json, "type", "");
        return anchor.get().type().name().equalsIgnoreCase(expected)
                || anchor.get().type().name().equalsIgnoreCase(expected.substring(expected.indexOf(':') + 1).replace('/', '_').toUpperCase(Locale.ROOT));
    }

    private static float anchorDistance(Moe moe, MoeAnchor anchor) {
        return (float) Math.sqrt(anchor.dimPos().getPos().distSqr(moe.blockPosition()));
    }

    private static Optional<MoeSocialContext> socialContext(Moe moe, JsonObject json) {
        return MoeSocialContext.find(moe, socialRadius(json));
    }

    private static double socialRadius(JsonObject json) {
        return GsonHelper.getAsDouble(json, "radius", 8.0D);
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
        String name = GsonHelper.getAsString(json, json.has("block") ? "block" : "name", "");
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
        String name = GsonHelper.getAsString(json, json.has("item") ? "item" : "name", "");
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

    private static boolean attentionKeyMatches(String actual, JsonObject json, String key) {
        if (json.has("value")) {
            return stringMatches(actual, json);
        }
        JsonObject wrapped = new JsonObject();
        wrapped.addProperty("value", GsonHelper.getAsString(json, key, ""));
        if (json.has("operation")) {
            wrapped.add("operation", json.get("operation"));
        }
        if (json.has("not")) {
            wrapped.add("not", json.get("not"));
        }
        return stringMatches(actual, wrapped);
    }

    private static MoeItemPreferences.PreferenceSignal heldItemSignal(Moe moe, JsonObject json) {
        ServerPlayer player = targetPlayer(moe);
        if (player == null) {
            return MoeItemPreferences.PreferenceSignal.neutral();
        }
        ItemStack stack = player.getItemInHand(hand(json));
        if (stack.isEmpty()) {
            return MoeItemPreferences.PreferenceSignal.neutral();
        }
        return MoeItemPreferences.signal(new SocialAffinities.Profile(
                moe.getActualBlockState(),
                moe.getBloodType(),
                moe.getDere(),
                moe.getZodiac(),
                moe.getGender(),
                moe.getEmotion()), stack);
    }

    private static boolean traitMatches(String actual, JsonObject json) {
        return stringMatches(actual, json);
    }

    private static boolean enumMatches(Enum<?> actual, JsonObject json) {
        String expected = GsonHelper.getAsString(json, "value", "");
        boolean pass = actual.name().equalsIgnoreCase(expected);
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
        String value = GsonHelper.getAsString(json, "hand", "main_hand").toUpperCase(Locale.ROOT);
        return InteractionHand.valueOf(value);
    }

    private static ServerPlayer targetPlayer(Moe moe) {
        if (!(moe.level() instanceof ServerLevel level)) {
            return null;
        }
        ServerPlayer target = level.getServer().getPlayerList().getPlayer(moe.getDialogueTarget());
        return target == null ? level.getServer().getPlayerList().getPlayer(moe.getPlayerUUID()) : target;
    }

    private static Optional<PlayerRelationship> targetRelationship(Moe moe) {
        if (!(moe.level() instanceof ServerLevel level)) {
            return Optional.empty();
        }
        return BlockPartyDB.get(level).findPlayerRelationshipSafe(moe.getDatabaseID(), targetPlayerUuid(moe));
    }

    private static Optional<AttentionRecord> attention(Moe moe, JsonObject json) {
        if (!(moe.level() instanceof ServerLevel level)) {
            return Optional.empty();
        }
        try {
            String type = GsonHelper.getAsString(json, "type", "");
            String source = GsonHelper.getAsString(json, "source", "");
            if (!type.isBlank() && !source.isBlank()) {
                return BlockPartyDB.get(level).findAttention(targetPlayerUuid(moe), type, source);
            }
            return BlockPartyDB.get(level).latestAttention(targetPlayerUuid(moe));
        } catch (RuntimeException | SQLException exception) {
            return Optional.empty();
        }
    }

    private static UUID targetPlayerUuid(Moe moe) {
        UUID target = moe.getDialogueTarget();
        return target.getMostSignificantBits() == 0L && target.getLeastSignificantBits() == 0L ? moe.getPlayerUUID() : target;
    }

    private static boolean maybeNegate(boolean pass, JsonObject json) {
        return GsonHelper.getAsBoolean(json, "not", false) ? !pass : pass;
    }
}
