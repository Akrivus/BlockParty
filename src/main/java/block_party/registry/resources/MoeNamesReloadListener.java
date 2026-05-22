package block_party.registry.resources;

import block_party.BlockParty;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public final class MoeNamesReloadListener implements PreparableReloadListener {
    private static final Map<String, List<String>> NAMES_BY_GENDER = new ConcurrentHashMap<>();
    private static final String DIRECTORY = "moes/names";

    public static List<String> names(String gender) {
        return List.copyOf(NAMES_BY_GENDER.getOrDefault(normalize(gender, "FEMALE"), List.of()));
    }

    public static int totalNameCount() {
        return NAMES_BY_GENDER.values().stream().mapToInt(List::size).sum();
    }

    public static String firstUnclaimed(String gender, List<String> claimed) {
        for (String name : names(gender)) {
            if (!claimed.contains(name)) {
                return name;
            }
        }
        return "Tokumei";
    }

    @Override
    public java.util.concurrent.CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager resourceManager,
            java.util.concurrent.Executor backgroundExecutor,
            java.util.concurrent.Executor gameExecutor) {
        return java.util.concurrent.CompletableFuture
                .supplyAsync(() -> load(resourceManager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(loaded -> {
                    NAMES_BY_GENDER.clear();
                    NAMES_BY_GENDER.putAll(loaded);
                }, gameExecutor);
    }

    private static Map<String, List<String>> load(ResourceManager resourceManager) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("block_party_moe_names");
        try {
            Map<String, List<String>> loaded = new ConcurrentHashMap<>();
            Map<ResourceLocation, Resource> resources = resourceManager.listResources(DIRECTORY, id -> id.getPath().endsWith(".json"));
            for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
                String gender = normalize(entry.getKey().getPath().substring((DIRECTORY + "/").length(), entry.getKey().getPath().length() - ".json".length()), "FEMALE");
                try (Reader reader = entry.getValue().openAsReader()) {
                    loaded.put(gender, parseNames(JsonParser.parseReader(reader).getAsJsonObject()));
                } catch (Exception exception) {
                    // Fails closed; malformed optional name files should not break reload.
                }
            }
            return loaded;
        } finally {
            profiler.pop();
        }
    }

    public static List<String> parseNames(JsonObject object) {
        JsonArray array = object.getAsJsonArray("names");
        List<String> names = new ArrayList<>();
        for (JsonElement element : array) {
            names.add(element.getAsString());
        }
        return List.copyOf(names);
    }

    public static List<String> safeParseNames(JsonObject object) {
        try {
            return parseNames(object);
        } catch (RuntimeException exception) {
            return List.of();
        }
    }

    private static String normalize(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String normalized = value.toUpperCase(java.util.Locale.ROOT);
        return switch (normalized) {
            case "MALE", "FEMALE", "NONBINARY" -> normalized;
            default -> fallback;
        };
    }
}
