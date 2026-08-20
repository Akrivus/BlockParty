package block_party.registry.resources;

import block_party.BlockParty;
import block_party.world.progression.ArrivalDefinition;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

public final class ArrivalReloadListener implements PreparableReloadListener {
    private static final String DIRECTORY = "moes/arrivals";
    private volatile List<ArrivalDefinition> definitions = List.of();

    public List<ArrivalDefinition> definitions() {
        return this.definitions;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager,
                                          Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> load(manager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(loaded -> this.definitions = loaded, gameExecutor);
    }

    private static List<ArrivalDefinition> load(ResourceManager manager) {
        List<ArrivalDefinition> loaded = new ArrayList<>();
        manager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json")).forEach((location, resource) -> {
            try (BufferedReader reader = resource.openAsReader()) {
                loaded.add(parse(id(location), JsonParser.parseReader(reader).getAsJsonObject()));
            } catch (RuntimeException | IOException exception) {
                BlockParty.LOGGER.error("Failed to load arrival definition {}", location, exception);
            }
        });
        return List.copyOf(loaded);
    }

    public static ArrivalDefinition parse(ResourceLocation id, JsonObject json) {
        JsonObject collected = GsonHelper.getAsJsonObject(json, "collected");
        JsonObject placed = GsonHelper.getAsJsonObject(json, "placed");
        JsonObject support = GsonHelper.getAsJsonObject(json, "support");
        return new ArrivalDefinition(
                id,
                collected.has("tag")
                        ? ArrivalDefinition.ItemSelector.tag(ResourceLocation.parse(GsonHelper.getAsString(collected, "tag")))
                        : ArrivalDefinition.ItemSelector.item(ResourceLocation.parse(GsonHelper.getAsString(collected, "item"))),
                Math.max(1, GsonHelper.getAsInt(json, "threshold", 64)),
                blockSelector(placed),
                blockSelector(support),
                BuiltInRegistries.BLOCK.getValue(ResourceLocation.parse(GsonHelper.getAsString(json, "result"))),
                Math.max(0.0D, GsonHelper.getAsDouble(json, "exclusion_radius", 24.0D)));
    }

    private static ArrivalDefinition.BlockSelector blockSelector(JsonObject json) {
        return json.has("tag")
                ? ArrivalDefinition.BlockSelector.tag(ResourceLocation.parse(GsonHelper.getAsString(json, "tag")))
                : ArrivalDefinition.BlockSelector.block(ResourceLocation.parse(GsonHelper.getAsString(json, "block")));
    }

    private static ResourceLocation id(ResourceLocation resource) {
        String path = resource.getPath().substring((DIRECTORY + "/").length());
        return ResourceLocation.fromNamespaceAndPath(resource.getNamespace(), path.substring(0, path.length() - 5));
    }
}
