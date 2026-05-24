package block_party.registry.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class MoeTextureReloadListener implements PreparableReloadListener {
    private static final String LEGACY_DATA_DIRECTORY = "moes/textures";
    private static final String BUNDLED_ASSET_DIRECTORY = "textures/moe";

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager,
                                          Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> load(manager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(MoeTextures::replaceOverrides, gameExecutor);
    }

    private static Map<Block, List<MoeTextures.Override>> load(ResourceManager manager) {
        Map<Block, List<MoeTextures.Override>> overrides = new HashMap<>();
        loadDirectory(manager, LEGACY_DATA_DIRECTORY, overrides);
        loadDirectory(manager, BUNDLED_ASSET_DIRECTORY, overrides);
        return overrides;
    }

    private static void loadDirectory(ResourceManager manager, String directory, Map<Block, List<MoeTextures.Override>> overrides) {
        manager.listResources(directory, path -> path.getPath().endsWith(".json")).forEach((location, resource) -> {
            try (BufferedReader reader = resource.openAsReader()) {
                safeParseOverride(JsonParser.parseReader(reader).getAsJsonObject())
                        .ifPresent(value -> overrides.computeIfAbsent(value.state().getBlock(), ignored -> new ArrayList<>()).add(value));
            } catch (RuntimeException | java.io.IOException ignored) {
                // Fails closed; malformed resources should not break the client reload.
            }
        });
    }

    public static void addOverride(Map<Block, List<MoeTextures.Override>> overrides, JsonObject json) {
        Optional<MoeTextures.Override> override = parseOverride(json);
        override.ifPresent(value -> overrides.computeIfAbsent(value.state().getBlock(), ignored -> new ArrayList<>()).add(value));
    }

    public static Optional<MoeTextures.Override> parseOverride(JsonObject json) {
        ResourceLocation blockId = ResourceLocation.parse(json.get("block").getAsString());
        Optional<Block> block = block(blockId);
        if (block.isEmpty()) {
            return Optional.empty();
        }
        BlockState defaultState = block.get().defaultBlockState();
        Map<String, Comparable<?>> properties = new HashMap<>();
        if (json.has("props")) {
            for (JsonElement element : json.getAsJsonArray("props")) {
                JsonObject propJson = element.getAsJsonObject();
                String name = propJson.get("name").getAsString();
                String value = propJson.get("value").getAsString();
                putProperty(defaultState, properties, name, value);
            }
        }
        ResourceLocation texture = ResourceLocation.parse(json.get("texture").getAsString());
        return Optional.of(new MoeTextures.Override(defaultState, properties, texture));
    }

    public static Optional<MoeTextures.Override> safeParseOverride(JsonObject json) {
        try {
            return parseOverride(json);
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }

    private static void putProperty(BlockState state, Map<String, Comparable<?>> properties, String name, String value) {
        for (Property<?> property : state.getProperties()) {
            if (property.getName().equals(name)) {
                Comparable<?> parsed = property.getValue(value).orElseThrow(() ->
                        new IllegalArgumentException("Invalid value " + value + " for property " + name));
                properties.put(name, parsed);
                return;
            }
        }
        throw new IllegalArgumentException("Unknown property " + name + " for block " + state.getBlock());
    }

    private static Optional<Block> block(ResourceLocation id) {
        return BuiltInRegistries.BLOCK.get(ResourceKey.create(Registries.BLOCK, id)).map(Holder.Reference::value);
    }
}
