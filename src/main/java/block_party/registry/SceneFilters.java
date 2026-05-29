package block_party.registry;

import block_party.BlockParty;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SceneFilters {
    public static final ResourceKey<Registry<Builder>> REGISTRY_KEY = ResourceKey.createRegistryKey(BlockParty.source("filters"));
    public static final DeferredRegister<Builder> FILTERS = DeferredRegister.create(REGISTRY_KEY, BlockParty.ID);
    public static final Registry<Builder> REGISTRY = FILTERS.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE - 1));
    public static final Map<String, DeferredHolder<Builder, Builder>> ENTRIES = new LinkedHashMap<>();

    static {
        registerAll(
                "always", "never", "is_corporeal", "is_cardinal", "if_raining", "if_sunny",
                "if_full_moon", "if_gibbous_moon", "if_half_moon", "if_crescent_moon", "if_new_moon",
                "if_morning", "if_noon", "if_evening", "if_night", "if_midnight", "if_dawn", "if_time",
                "if_blood_type_ab", "if_blood_type_b", "if_blood_type_a", "if_blood_type_o",
                "if_remembers_place", "if_remembers_house", "if_remembers_shelter", "if_remembers_garden",
                "if_remembers_grove", "if_remembers_field", "if_remembers_workshop",
                "if_remembers_waterfront", "if_remembers_cave", "if_remembers_shrine", "if_remembers_farm",
                "if_at_remembered_place", "if_remembered_place_overcrowded", "if_remembered_place_invalid",
                "if_has_environmental_observation", "if_observed_awe", "if_observed_affinity",
                "if_observed_tension", "if_has_gift_memory", "if_liked_gift", "if_disliked_gift",
                "if_interesting_gift", "if_begged_for_gift", "if_social_place", "if_social_place_share",
                "if_social_place_orbit", "if_social_place_guard", "if_social_place_avoid",
                "if_sheltering_from_rain",
                "if_himedere", "if_kuudere", "if_tsundere", "if_yandere", "if_deredere", "if_dandere",
                "if_angry", "if_begging", "if_confused", "if_crying", "if_mischievous", "if_embarrassed",
                "if_happy", "if_normal", "if_pained", "if_psychotic", "if_scared", "if_sick",
                "if_snooty", "if_smitten", "if_tired", "if_male", "if_female", "if_nonbinary",
                "self", "health", "food_level", "loyalty", "stress", "player_counter",
                "has_attention", "attention_type", "attention_source",
                "attention_item", "attention_count", "attention_block",
                "player_has_cookie", "player_held_item", "player_has_item", "counter", "has_cookie", "held_item",
                "has_item", "moe_has_item", "block",
                "family_name", "name", "has_social_target", "social_affinity", "social_tension",
                "social_interest", "social_visual", "social_reaction", "social_target_name",
                "social_target_block", "social_target_blood_type", "social_target_dere",
                "social_target_zodiac", "social_target_emotion",
                "remembered_place_type", "remembered_place_score", "remembered_place_occupancy",
                "remembered_place_capacity", "remembered_place_anchor_type", "observed_block",
                "observed_signal_layer", "observed_affinity", "observed_tension", "observed_interest",
                "gift_preference", "gift_aversion", "gift_interest", "gift_begging", "gift_item",
                "held_item_preference", "held_item_begging", "social_place_behavior", "social_place_type",
                "social_place_distance", "social_place_owner_name");
    }

    private SceneFilters() {
    }

    private static void registerAll(String... ids) {
        for (String id : ids) {
            ENTRIES.put(id, FILTERS.register(id, () -> new Builder(id)));
        }
    }

    public static void register(IEventBus modBus) {
        FILTERS.register(modBus);
    }

    public record Builder(String id) {
    }
}
