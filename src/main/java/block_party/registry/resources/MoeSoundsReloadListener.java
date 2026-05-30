package block_party.registry.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;

public final class MoeSoundsReloadListener implements PreparableReloadListener {
    private static final Map<Block, Map<String, SoundEvent>> OVERRIDES = new HashMap<>();

    public static int size() {
        return OVERRIDES.values().stream().mapToInt(Map::size).sum();
    }

    public static SoundEvent get(Block block, String soundName) {
        Map<String, SoundEvent> sounds = OVERRIDES.get(block);
        if (sounds != null && sounds.containsKey(soundName)) {
            return sounds.get(soundName);
        }
        return BuiltInRegistries.SOUND_EVENT.getValue(ResourceLocation.fromNamespaceAndPath("block_party", "moe." + soundName));
    }

    public static Map<String, SoundEvent> parseSounds(JsonObject json) {
        Map<String, SoundEvent> sounds = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            sound(ResourceLocation.parse(entry.getValue().getAsString())).ifPresent(sound -> sounds.put(entry.getKey(), sound));
        }
        return sounds;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager,
                                          Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> load(manager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(sounds -> {
                    OVERRIDES.clear();
                    OVERRIDES.putAll(sounds);
                }, gameExecutor);
    }

    private static Map<Block, Map<String, SoundEvent>> load(ResourceManager manager) {
        Map<Block, Map<String, SoundEvent>> overrides = new HashMap<>();
        manager.listResources("moes/sounds", path -> path.getPath().endsWith(".json")).forEach((location, resource) -> {
            ResourceLocation blockId = dataDrivenId(location, "moes/sounds/");
            Optional<Block> block = block(blockId);
            if (block.isEmpty()) {
                return;
            }
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                Map<String, SoundEvent> sounds = parseSounds(json);
                if (!sounds.isEmpty()) {
                    overrides.put(block.get(), sounds);
                }
            } catch (RuntimeException | IOException ignored) {
                // Fails closed; missing optional sound overrides fall back to default Moe sounds.
            }
        });
        return overrides;
    }

    private static ResourceLocation dataDrivenId(ResourceLocation resourceLocation, String prefix) {
        String path = resourceLocation.getPath();
        String suffix = ".json";
        if (path.startsWith(prefix)) {
            path = path.substring(prefix.length());
        }
        if (path.endsWith(suffix)) {
            path = path.substring(0, path.length() - suffix.length());
        }
        return ResourceLocation.fromNamespaceAndPath(resourceLocation.getNamespace(), path);
    }

    private static Optional<Block> block(ResourceLocation id) {
        return BuiltInRegistries.BLOCK.get(ResourceKey.create(Registries.BLOCK, id)).map(Holder.Reference::value);
    }

    private static Optional<SoundEvent> sound(ResourceLocation id) {
        Optional<SoundEvent> direct = BuiltInRegistries.SOUND_EVENT.get(ResourceKey.create(Registries.SOUND_EVENT, id)).map(Holder.Reference::value);
        if (direct.isPresent() || !"block_party".equals(id.getNamespace()) || !id.getPath().startsWith("moe/")) {
            return direct;
        }
        ResourceLocation dotted = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), id.getPath().replace('/', '.'));
        return BuiltInRegistries.SOUND_EVENT.get(ResourceKey.create(Registries.SOUND_EVENT, dotted)).map(Holder.Reference::value);
    }
}
