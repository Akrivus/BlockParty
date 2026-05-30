package block_party.registry.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class BlockAliasesReloadListener implements PreparableReloadListener {
    private static final Map<Block, Block> ALIASES = new HashMap<>();

    public static BlockState resolve(BlockState state) {
        if (state == null) {
            return Blocks.AIR.defaultBlockState();
        }
        Block alias = ALIASES.getOrDefault(state.getBlock(), state.getBlock());
        return alias.defaultBlockState();
    }

    public static int size() {
        return ALIASES.size();
    }

    public static Map<Block, Block> parseAlias(ResourceLocation aliasId, JsonObject json) {
        Map<Block, Block> aliases = new HashMap<>();
        Optional<Block> aliasBlock = block(aliasId);
        if (aliasBlock.isEmpty()) {
            return aliases;
        }
        for (JsonElement element : json.getAsJsonArray("aliases")) {
            block(ResourceLocation.parse(element.getAsString())).ifPresent(source -> aliases.put(source, aliasBlock.get()));
        }
        return aliases;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager,
                                          Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> load(manager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(aliases -> {
                    ALIASES.clear();
                    ALIASES.putAll(aliases);
                }, gameExecutor);
    }

    private static Map<Block, Block> load(ResourceManager manager) {
        Map<Block, Block> aliases = new HashMap<>();
        manager.listResources("moes/aliases", path -> path.getPath().endsWith(".json")).forEach((location, resource) -> {
            ResourceLocation aliasId = aliasId(location);
            Optional<Block> aliasBlock = block(aliasId);
            if (aliasBlock.isEmpty()) {
                return;
            }
            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                aliases.putAll(parseAlias(aliasId, json));
            } catch (RuntimeException | IOException ignored) {
                // The counting listener still catches malformed JSON; this listener fails closed.
            }
        });
        return aliases;
    }

    private static ResourceLocation aliasId(ResourceLocation resourceLocation) {
        String path = resourceLocation.getPath();
        String prefix = "moes/aliases/";
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
}
