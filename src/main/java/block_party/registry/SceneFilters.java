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
                "if_himedere", "if_kuudere", "if_tsundere", "if_yandere", "if_deredere", "if_dandere",
                "if_angry", "if_begging", "if_confused", "if_crying", "if_mischievous", "if_embarrassed",
                "if_happy", "if_normal", "if_pained", "if_psychotic", "if_scared", "if_sick",
                "if_snooty", "if_smitten", "if_tired", "if_male", "if_female", "if_nonbinary",
                "self", "health", "food_level", "loyalty", "stress", "player_counter",
                "player_has_cookie", "player_held_item", "counter", "has_cookie", "held_item", "block",
                "family_name", "name");
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
