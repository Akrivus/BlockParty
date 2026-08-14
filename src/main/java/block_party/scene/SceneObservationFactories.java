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
import block_party.world.progression.SamuraiProgression;
import com.google.gson.JsonObject;
import java.sql.SQLException;
import java.util.List;
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
            return diagnostic(type, json, simple);
        }
        SceneObservation built = switch (type.getPath()) {
            case "if_time" -> moe -> compare((int) (moe.level().getDayTime() % 24000L), json);
            case "elapsed_since_marker" -> moe -> SceneTimeMarkers.elapsed(
                    moe,
                    GsonHelper.getAsString(json, "name", ""),
                    variableScope(json, SceneVariableScope.NPC),
                    markerGameTicks(json),
                    markerRealMillis(json));
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
            case "samurai_armor_piece" -> moe -> targetPlayer(moe) != null && maybeNegate(
                    SamuraiProgression.hasPiece(targetPlayer(moe), SamuraiProgression.Piece.fromValue(
                            GsonHelper.getAsString(json, "piece", GsonHelper.getAsString(json, "value", "")))), json);
            case "samurai_complete_armor" -> moe -> targetPlayer(moe) != null
                    && maybeNegate(SamuraiProgression.hasCompleteArmor(targetPlayer(moe)), json);
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
            case "player_counter" -> moe -> counterMatches(moe, json, SceneVariableScope.PLAYER);
            case "player_has_cookie" -> moe -> cookieMatches(moe, json, SceneVariableScope.PLAYER);
            case "world_counter" -> moe -> counterMatches(moe, json, SceneVariableScope.WORLD);
            case "world_has_cookie" -> moe -> cookieMatches(moe, json, SceneVariableScope.WORLD);
            case "family_name" -> moe -> stringMatches(moe.getFamilyName(), json);
            default -> FAIL_CLOSED;
        };
        return diagnostic(type, json, built);
    }

    private static SceneObservation diagnostic(ResourceLocation type, JsonObject json, SceneObservation observation) {
        return new SceneObservation() {
            @Override
            public boolean verify(Moe moe) {
                return observation.verify(moe);
            }

            @Override
            public DiagnosticResult diagnose(Moe moe) {
                if (this.verify(moe)) {
                    return DiagnosticResult.pass();
                }
                return DiagnosticResult.fail(reason(type, json, moe));
            }
        };
    }

    private static String reason(ResourceLocation type, JsonObject json, Moe moe) {
        String path = type.getPath();
        return switch (path) {
            case "health" -> compareReason("health", moe.getHealth(), json);
            case "food_level" -> compareReason("energy", moe.getFoodLevel(), json);
            case "loyalty" -> compareReason("loyalty", moe.getLoyalty(), json);
            case "stress" -> compareReason("stress", moe.getStress(), json);
            case "target_affection" -> relationshipReason(moe, "affection", json);
            case "target_loyalty" -> relationshipReason(moe, "loyalty", json);
            case "target_trust" -> relationshipReason(moe, "trust", json);
            case "target_relationship_stress" -> relationshipReason(moe, "relationship stress", json);
            case "target_yearbook_signed" -> booleanReason("yearbook unlocked", targetRelationship(moe).map(PlayerRelationship::yearbookSigned), json);
            case "target_phone_contact" -> booleanReason("phone contact unlocked", targetRelationship(moe).map(PlayerRelationship::phoneContact), json);
            case "counter" -> counterReason(moe, json, variableScope(json, SceneVariableScope.NPC), "counter");
            case "player_counter" -> counterReason(moe, json, SceneVariableScope.PLAYER, "player counter");
            case "world_counter" -> counterReason(moe, json, SceneVariableScope.WORLD, "world counter");
            case "has_cookie" -> cookieReason(moe, json, variableScope(json, SceneVariableScope.NPC), "flag");
            case "player_has_cookie" -> cookieReason(moe, json, SceneVariableScope.PLAYER, "player flag");
            case "world_has_cookie" -> cookieReason(moe, json, SceneVariableScope.WORLD, "world flag");
            case "held_item" -> itemReason("Moe held item", moe.getItemInHand(hand(json)), json);
            case "player_held_item" -> targetPlayer(moe) == null
                    ? "target player is offline"
                    : itemReason("player held item", targetPlayer(moe).getItemInHand(hand(json)), json);
            case "has_item", "moe_has_item" -> itemInventoryReason("Moe inventory", json);
            case "player_has_item" -> targetPlayer(moe) == null ? "target player is offline" : itemInventoryReason("player inventory", json);
            case "block" -> "requires block " + expectedName(json, "block") + ", current " + blockKey(moe.getVisibleBlockState());
            case "name" -> stringReason("name", moe.getGivenName(), json);
            case "family_name" -> stringReason("family name", moe.getFamilyName(), json);
            case "follow_intent" -> enumReason("follow intent", moe.getFollowIntent(), json);
            case "follow_ticks_remaining" -> compareReason("follow ticks remaining", moe.getFollowTicksRemaining(), json);
            case "elapsed_since_marker" -> "time marker " + GsonHelper.getAsString(json, "name", "") + " has not elapsed";
            case "follow_player_is_target" -> "follow player " + moe.getFollowPlayerUUID() + " is not dialogue target " + targetPlayerUuid(moe);
            case "has_anchor" -> currentAnchor(moe).isEmpty() ? "missing routine anchor" : "routine anchor does not match " + expectedType(json);
            case "anchor_type" -> currentAnchor(moe)
                    .map(anchor -> enumReason("anchor type", anchor.type(), json))
                    .orElse("missing routine anchor");
            case "anchor_distance" -> currentAnchor(moe)
                    .map(anchor -> compareReason("anchor distance", anchorDistance(moe, anchor), json))
                    .orElse("missing routine anchor");
            case "anchor_priority" -> currentAnchor(moe)
                    .map(anchor -> compareReason("anchor priority", anchor.priority(), json))
                    .orElse("missing routine anchor");
            case "anchor_player_owned" -> currentAnchor(moe)
                    .map(anchor -> "anchor owner " + anchor.playerUuid() + " does not match Moe owner " + moe.getPlayerUUID())
                    .orElse("missing routine anchor");
            case "routine_intent" -> enumReason("routine intent", moe.getEffectiveRoutineIntent(), json);
            case "explicit_routine_intent" -> enumReason("explicit routine intent", moe.getRoutineIntent(), json);
            case "remembered_place_type" -> moe.rememberedPlace()
                    .map(place -> enumReason("remembered place", place.type(), json))
                    .orElse("missing remembered place");
            case "remembered_place_score" -> moe.rememberedPlace()
                    .map(place -> compareReason("remembered place score", (float) place.score(), json))
                    .orElse("missing remembered place");
            case "remembered_place_occupancy" -> moe.rememberedPlace()
                    .map(place -> compareReason("remembered place occupancy", place.occupancy(), json))
                    .orElse("missing remembered place");
            case "remembered_place_capacity" -> moe.rememberedPlace()
                    .map(place -> compareReason("remembered place capacity", place.capacity(), json))
                    .orElse("missing remembered place");
            case "observed_block" -> moe.latestEnvironmentalObservation()
                    .map(observation -> "requires observed block " + expectedName(json, "block") + ", current " + blockKey(observation.state()))
                    .orElse("missing environmental observation");
            case "observed_affinity" -> moe.latestEnvironmentalObservation()
                    .map(observation -> compareReason("observed affinity", observation.signal().affinity(), json))
                    .orElse("missing environmental observation");
            case "observed_tension" -> moe.latestEnvironmentalObservation()
                    .map(observation -> compareReason("observed tension", observation.signal().tension(), json))
                    .orElse("missing environmental observation");
            case "observed_interest" -> moe.latestEnvironmentalObservation()
                    .map(observation -> compareReason("observed interest", observation.signal().interest(), json))
                    .orElse("missing environmental observation");
            case "gift_preference" -> giftReason(moe, "gift preference", json);
            case "gift_aversion" -> giftReason(moe, "gift aversion", json);
            case "gift_interest" -> giftReason(moe, "gift interest", json);
            case "gift_begging" -> giftReason(moe, "gift begging", json);
            case "gift_item" -> moe.latestGiftItem().map(stack -> itemReason("gift item", stack, json)).orElse("missing gift item memory");
            default -> genericReason(path, moe);
        };
    }

    private static String relationshipReason(Moe moe, String field, JsonObject json) {
        Optional<PlayerRelationship> relationship = targetRelationship(moe);
        if (relationship.isEmpty()) {
            return "missing relationship row for player " + targetPlayerUuid(moe);
        }
        PlayerRelationship row = relationship.get();
        float actual = switch (field) {
            case "affection" -> row.affection();
            case "loyalty" -> row.loyalty();
            case "trust" -> row.trust();
            default -> row.stress();
        };
        return compareReason("requires " + field, actual, json);
    }

    private static String counterReason(Moe moe, JsonObject json, SceneVariableScope scope, String label) {
        String name = GsonHelper.getAsString(json, "name", "");
        Integer value = scopedVariables(moe, scope).counters().get(name);
        return compareReason(label + " " + name, value == null ? 0 : value, json);
    }

    private static String cookieReason(Moe moe, JsonObject json, SceneVariableScope scope, String label) {
        String name = GsonHelper.getAsString(json, "name", "");
        String value = scopedVariables(moe, scope).cookies().get(name);
        if (value == null) {
            return "missing " + label + ": " + name;
        }
        if (json.has("value")) {
            return stringReason(label + " " + name, value, json);
        }
        return "unexpected " + label + ": " + name;
    }

    private static String giftReason(Moe moe, String label, JsonObject json) {
        if (moe.latestGiftPreferenceSignal().isEmpty()) {
            return "missing gift preference memory";
        }
        var signal = moe.latestGiftPreferenceSignal().get();
        float actual = switch (label) {
            case "gift preference" -> signal.preference();
            case "gift aversion" -> signal.aversion();
            case "gift interest" -> signal.interest();
            default -> signal.begging();
        };
        return compareReason(label, actual, json);
    }

    private static String booleanReason(String label, Optional<Boolean> actual, JsonObject json) {
        if (actual.isEmpty()) {
            return "missing relationship row";
        }
        boolean expected = !GsonHelper.getAsBoolean(json, "not", false);
        return "requires " + label + "=" + expected + ", current " + actual.get();
    }

    private static String itemReason(String label, ItemStack stack, JsonObject json) {
        String expected = expectedName(json, "item");
        String actual = stack.isEmpty() ? "empty" : BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        String count = json.has("count") ? ", count " + stack.getCount() + " vs " + json.get("count") : "";
        return "requires " + label + ": " + expected + ", current " + actual + count;
    }

    private static String itemInventoryReason(String label, JsonObject json) {
        return label + " missing required item " + expectedName(json, "item");
    }

    private static String stringReason(String label, String actual, JsonObject json) {
        return "requires " + label + " " + operation(json) + " " + GsonHelper.getAsString(json, "value", "")
                + ", current " + (actual == null ? "" : actual);
    }

    private static String enumReason(String label, Enum<?> actual, JsonObject json) {
        return "requires " + label + "=" + GsonHelper.getAsString(json, "value", "") + ", current " + actual.name();
    }

    private static String compareReason(String label, float actual, JsonObject json) {
        return label + " " + operation(json) + " " + GsonHelper.getAsFloat(json, "value", 0.0F) + ", current " + actual;
    }

    private static String operation(JsonObject json) {
        return GsonHelper.getAsString(json, "operation", "equals");
    }

    private static String expectedType(JsonObject json) {
        return GsonHelper.getAsString(json, "type", GsonHelper.getAsString(json, "value", ""));
    }

    private static String expectedName(JsonObject json, String preferredKey) {
        return GsonHelper.getAsString(json, json.has(preferredKey) ? preferredKey : "name", "");
    }

    private static String blockKey(BlockState state) {
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
    }

    private static String genericReason(String path, Moe moe) {
        return "filter failed: " + path + " for " + moe.getGivenName() + " #" + moe.getDatabaseID();
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
        return counterMatches(moe, json, variableScope(json, SceneVariableScope.NPC));
    }

    private static long markerGameTicks(JsonObject json) {
        long ticks = GsonHelper.getAsLong(json, "min_game_ticks", 0L);
        ticks += GsonHelper.getAsLong(json, "min_game_days", 0L) * 24000L;
        return ticks;
    }

    private static long markerRealMillis(JsonObject json) {
        long millis = GsonHelper.getAsLong(json, "min_real_millis", 0L);
        millis += GsonHelper.getAsLong(json, "min_real_seconds", 0L) * 1000L;
        millis += GsonHelper.getAsLong(json, "min_real_minutes", 0L) * 60_000L;
        millis += GsonHelper.getAsLong(json, "min_real_days", 0L) * 86_400_000L;
        return millis;
    }

    private static boolean counterMatches(Moe moe, JsonObject json, SceneVariableScope scope) {
        Integer value = scopedVariables(moe, scope)
                .counters()
                .get(GsonHelper.getAsString(json, "name", ""));
        return compare(value == null ? 0 : value, json);
    }

    private static boolean cookieMatches(Moe moe, JsonObject json) {
        return cookieMatches(moe, json, variableScope(json, SceneVariableScope.NPC));
    }

    private static boolean cookieMatches(Moe moe, JsonObject json, SceneVariableScope scope) {
        String value = scopedVariables(moe, scope)
                .cookies()
                .get(GsonHelper.getAsString(json, "name", ""));
        if (!json.has("value")) {
            return value != null;
        }
        return stringMatches(value, json);
    }

    private static SceneVariableStore scopedVariables(Moe moe, SceneVariableScope scope) {
        SceneVariables variables = SceneVariables.get(moe.level());
        return switch (scope) {
            case NPC -> variables.npc(moe.getDatabaseID());
            case PLAYER -> variables.player(targetPlayerUuid(moe));
            case WORLD -> variables.world();
        };
    }

    private static SceneVariableScope variableScope(JsonObject json, SceneVariableScope fallback) {
        String key = json.has("scope") ? "scope" : "target";
        return SceneVariableScope.fromValue(GsonHelper.getAsString(json, key, fallback.serializedName()), fallback);
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
