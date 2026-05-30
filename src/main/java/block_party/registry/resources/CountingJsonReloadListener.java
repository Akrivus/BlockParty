package block_party.registry.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;

public final class CountingJsonReloadListener implements PreparableReloadListener {
    private static final Map<String, Integer> LOADED_COUNTS = new ConcurrentHashMap<>();
    private final String directory;

    public CountingJsonReloadListener(String directory) {
        this.directory = directory;
    }

    public static int loadedCount(String directory) {
        return LOADED_COUNTS.getOrDefault(directory, 0);
    }

    @Override
    public CompletableFuture<Void> reload(
            PreparationBarrier barrier,
            ResourceManager resourceManager,
            Executor backgroundExecutor,
            Executor gameExecutor) {
        return CompletableFuture
                .supplyAsync(() -> load(resourceManager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(count -> LOADED_COUNTS.put(this.directory, count), gameExecutor);
    }

    private int load(ResourceManager resourceManager) {
        ProfilerFiller profiler = Profiler.get();
        profiler.push("block_party_" + this.directory.replace('/', '_'));
        try {
            int count = 0;
            Map<ResourceLocation, Resource> resources = resourceManager.listResources(this.directory, id -> id.getPath().endsWith(".json"));
            for (Resource resource : resources.values()) {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement parsed = JsonParser.parseReader(reader);
                    if (!parsed.isJsonObject() && !parsed.isJsonArray()) {
                        throw new IllegalStateException("Expected JSON object or array in " + resource.sourcePackId());
                    }
                    count++;
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to parse bundled Block Party resource in " + this.directory, e);
                }
            }
            return count;
        } finally {
            profiler.pop();
        }
    }
}
