package block_party.conversation.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SceneFilterCatalog {
    private static final Set<String> FLAG_FILTERS = Set.of(
            "always", "never", "is_corporeal", "is_cardinal", "is_following",
            "can_follow_across_dimensions", "if_raining", "if_sunny", "if_full_moon",
            "if_gibbous_moon", "if_half_moon", "if_crescent_moon", "if_new_moon",
            "if_morning", "if_noon", "if_evening", "if_night", "if_midnight", "if_dawn",
            "if_blood_type_ab", "if_blood_type_b", "if_blood_type_a", "if_blood_type_o",
            "if_himedere", "if_kuudere", "if_tsundere", "if_yandere", "if_deredere",
            "if_dandere", "if_angry", "if_begging", "if_confused", "if_crying",
            "if_mischievous", "if_embarrassed", "if_happy", "if_normal", "if_pained",
            "if_psychotic", "if_scared", "if_sick", "if_snooty", "if_smitten", "if_tired",
            "if_male", "if_female", "if_nonbinary", "if_has_gift_memory", "if_liked_gift",
            "if_disliked_gift", "if_interesting_gift", "if_begged_for_gift",
            "if_has_environmental_observation", "if_sheltering_from_rain", "has_attention",
            "has_social_target", "has_anchor", "follow_player_is_target", "anchor_player_owned",
            "target_yearbook_signed", "target_phone_contact", "samurai_complete_armor");
    private static final Map<String, List<String>> ENUM_FILTERS = Map.ofEntries(
            Map.entry("routine_intent", List.of("idle", "relax", "rest", "sleep", "gather", "visit", "worship", "chore")),
            Map.entry("explicit_routine_intent", List.of("idle", "relax", "rest", "sleep", "gather", "visit", "worship", "chore")),
            Map.entry("dere", List.of("dandere", "deredere", "himedere", "kuudere", "nyandere", "tsundere", "yandere")),
            Map.entry("blood_type", List.of("a", "b", "ab", "o")),
            Map.entry("zodiac", List.of("aries", "taurus", "gemini", "cancer", "leo", "virgo", "libra", "scorpio", "sagittarius", "capricorn", "aquarius", "pisces")),
            Map.entry("follow_intent", List.of("phone_call", "party_invite", "follow_request", "come_here", "wait", "dismiss")),
            Map.entry("anchor_type", List.of("home", "garden", "location", "sapling", "shrine")));
    private static final Set<String> VALUE_FILTERS = Set.of(
            "name", "family_name", "social_target_name", "social_target_block",
            "social_target_blood_type", "social_target_dere", "social_target_zodiac",
            "social_target_emotion", "social_visual", "social_reaction", "attention_type",
            "attention_source", "remembered_place_type", "remembered_place_anchor_type",
            "observed_signal_layer", "gift_preference", "gift_aversion", "gift_interest",
            "gift_begging", "social_place_behavior", "social_place_type");
    private static final Set<String> NUMERIC_FILTERS = Set.of(
            "if_time", "health", "food_level", "loyalty", "stress", "target_affection",
            "target_loyalty", "target_trust", "target_relationship_stress", "player_counter",
            "world_counter", "counter", "attention_count", "follow_ticks_remaining",
            "anchor_distance", "anchor_priority", "social_affinity", "social_tension",
            "social_interest", "remembered_place_score", "remembered_place_occupancy",
            "remembered_place_capacity", "observed_affinity", "observed_tension", "observed_interest",
            "social_place_distance");
    private static final Set<String> RESOURCE_FILTERS = Set.of(
            "attention_item", "attention_block", "player_held_item", "player_has_item", "held_item",
            "has_item", "moe_has_item", "block", "observed_block", "gift_item", "held_item_preference");
    private static final Set<String> OTHER_FILTERS = Set.of(
            "elapsed_since_marker", "if_remembers_place", "if_remembers_house", "if_remembers_shelter",
            "if_remembers_garden", "if_remembers_grove", "if_remembers_field", "if_remembers_workshop",
            "if_remembers_waterfront", "if_remembers_cave", "if_remembers_shrine", "if_remembers_farm",
            "if_remembered_place_has_garden_lantern", "if_remembered_place_has_lit_garden_lantern",
            "if_remembered_place_has_unlit_garden_lantern", "if_at_remembered_place",
            "if_remembered_place_overcrowded", "if_remembered_place_invalid", "if_observed_awe",
            "if_observed_affinity", "if_observed_tension", "if_social_place", "if_social_place_share",
            "if_social_place_orbit", "if_social_place_guard", "if_social_place_avoid", "self",
            "world_has_cookie", "player_has_cookie", "has_cookie", "attention_source", "follow_intent",
            "follow_player_is_target", "anchor_type", "samurai_armor_piece", "social_target_block",
            "social_visual", "social_reaction", "remembered_place_type", "remembered_place_anchor_type",
            "observed_signal_layer", "gift_preference", "gift_aversion", "gift_interest", "gift_begging",
            "held_item_begging", "social_place_behavior", "social_place_type", "social_place_owner_name");

    private SceneFilterCatalog() {}

    public static boolean known(String type) {
        String id = path(type);
        return FLAG_FILTERS.contains(id) || ENUM_FILTERS.containsKey(id) || VALUE_FILTERS.contains(id)
                || NUMERIC_FILTERS.contains(id) || RESOURCE_FILTERS.contains(id) || OTHER_FILTERS.contains(id);
    }

    public static String validate(JsonObject filter) {
        if (filter == null || !filter.has("type") || !filter.get("type").isJsonPrimitive()) return "Scene filter type is required.";
        String type = path(filter.get("type").getAsString());
        if (!known(type)) return "Unknown scene filter '" + filter.get("type").getAsString() + "'.";
        if (ENUM_FILTERS.containsKey(type)) {
            String value = string(filter, "value");
            if (!ENUM_FILTERS.get(type).contains(value.toLowerCase(java.util.Locale.ROOT))) {
                return "Scene filter '" + type + "' requires one of " + ENUM_FILTERS.get(type) + ".";
            }
        } else if ((VALUE_FILTERS.contains(type) || NUMERIC_FILTERS.contains(type)) && !filter.has("value")) {
            return "Scene filter '" + type + "' requires value.";
        } else if (RESOURCE_FILTERS.contains(type) && !filter.has("item") && !filter.has("block") && !filter.has("value")) {
            return "Scene filter '" + type + "' requires an item, block, or value resource.";
        }
        return null;
    }

    public static List<String> types() {
        return java.util.stream.Stream.of(FLAG_FILTERS, ENUM_FILTERS.keySet(), VALUE_FILTERS, NUMERIC_FILTERS, RESOURCE_FILTERS, OTHER_FILTERS)
                .flatMap(Set::stream).sorted().map(id -> "block_party:" + id).toList();
    }

    public static Map<String, List<String>> enums() { return ENUM_FILTERS; }

    private static String path(String value) {
        if (value == null) return "";
        int colon = value.indexOf(':');
        return colon < 0 ? value : value.substring(colon + 1);
    }

    private static String string(JsonObject object, String name) {
        JsonElement value = object.get(name);
        return value == null || value.isJsonNull() ? "" : value.getAsString();
    }
}
