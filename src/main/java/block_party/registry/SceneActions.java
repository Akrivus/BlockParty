package block_party.registry;

import block_party.BlockParty;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SceneActions {
    public static final ResourceKey<Registry<Builder>> REGISTRY_KEY = ResourceKey.createRegistryKey(BlockParty.source("actions"));
    public static final DeferredRegister<Builder> ACTIONS = DeferredRegister.create(REGISTRY_KEY, BlockParty.ID);
    public static final Registry<Builder> REGISTRY = ACTIONS.makeRegistry(builder -> builder.maxId(Integer.MAX_VALUE - 1));
    public static final Map<String, DeferredHolder<Builder, Builder>> ENTRIES = new LinkedHashMap<>();

    static {
        registerAll(
                "send_dialogue", "send_response", "health", "food_level", "loyalty", "stress",
                "cookie", "counter", "hide", "create_voicemail", "start_follow_session",
                "clear_follow_session", "go_to_anchor", "set_home_to_anchor", "set_routine_intent",
                "clear_routine_intent", "sleep_at_home", "open_inventory", "give_item", "take_item",
                "wait", "dismiss", "end");
    }

    private SceneActions() {
    }

    private static void registerAll(String... ids) {
        for (String id : ids) {
            ENTRIES.put(id, ACTIONS.register(id, () -> new Builder(id)));
        }
    }

    public static void register(IEventBus modBus) {
        ACTIONS.register(modBus);
    }

    public record Builder(String id) {
    }
}
