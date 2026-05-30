package block_party.entities.profile;

import block_party.BlockParty;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public final class MoeFamilyNames {
    private static final String LANGUAGE_RESOURCE = "assets/block_party/lang/en_us.json";
    private static final Map<String, String> BUNDLED_EN_US = loadBundledLanguage();

    private MoeFamilyNames() {
    }

    public static String key(BlockState state) {
        ResourceLocation block = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        return "entity." + BlockParty.ID + "." + block.getNamespace() + "." + block.getPath();
    }

    public static Component component(BlockState state) {
        return Component.translatableWithFallback(key(state), fallback(state));
    }

    public static String get(BlockState state) {
        return BUNDLED_EN_US.getOrDefault(key(state), fallback(state));
    }

    public static String get(BlockState state, Map<String, String> translations) {
        return translations.getOrDefault(key(state), fallback(state));
    }

    static Map<String, String> loadBundledLanguage() {
        try (InputStream stream = MoeFamilyNames.class.getClassLoader().getResourceAsStream(LANGUAGE_RESOURCE)) {
            if (stream == null) {
                return Map.of();
            }
            JsonObject json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            Map<String, String> translations = new ConcurrentHashMap<>();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getValue().isJsonPrimitive() && entry.getKey().startsWith("entity." + BlockParty.ID + ".")) {
                    translations.put(entry.getKey(), entry.getValue().getAsString());
                }
            }
            return Map.copyOf(translations);
        } catch (RuntimeException | IOException exception) {
            return Map.of();
        }
    }

    private static String fallback(BlockState state) {
        return Component.translatable(state.getBlock().getDescriptionId()).getString();
    }
}
