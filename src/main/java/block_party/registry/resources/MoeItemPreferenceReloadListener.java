package block_party.registry.resources;

import block_party.entities.preferences.MoeItemPreferences;
import block_party.entities.social.SocialAffinities;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

public final class MoeItemPreferenceReloadListener implements PreparableReloadListener {
    private static final String DIRECTORY = "moes/item_preferences";

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager,
                                          Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> load(manager), backgroundExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(MoeItemPreferences::replaceRules, gameExecutor);
    }

    private static List<MoeItemPreferences.Rule> load(ResourceManager manager) {
        List<MoeItemPreferences.Rule> rules = new ArrayList<>();
        manager.listResources(DIRECTORY, path -> path.getPath().endsWith(".json")).forEach((location, resource) -> {
            try (BufferedReader reader = resource.openAsReader()) {
                rules.addAll(safeParseRules(JsonParser.parseReader(reader).getAsJsonObject()));
            } catch (RuntimeException | IOException ignored) {
                // Malformed preference resources fail closed so tuning datapacks remain isolated.
            }
        });
        return List.copyOf(rules);
    }

    public static List<MoeItemPreferences.Rule> safeParseRules(JsonObject json) {
        try {
            return parseRules(json);
        } catch (RuntimeException exception) {
            return List.of();
        }
    }

    public static List<MoeItemPreferences.Rule> parseRules(JsonObject json) {
        JsonArray array = json.has("rules") ? GsonHelper.getAsJsonArray(json, "rules") : new JsonArray();
        List<MoeItemPreferences.Rule> rules = new ArrayList<>();
        for (JsonElement element : array) {
            parseRule(GsonHelper.convertToJsonObject(element, "Moe item preference rule")).ifPresent(rules::add);
        }
        return List.copyOf(rules);
    }

    private static Optional<MoeItemPreferences.Rule> parseRule(JsonObject json) {
        Optional<SocialAffinities.Matcher> observer = parseObserver(GsonHelper.getAsJsonObject(json, "observer"));
        Optional<MoeItemPreferences.ItemMatcher> item = parseItem(json);
        if (observer.isEmpty() || item.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new MoeItemPreferences.Rule(
                observer.get(),
                item.get(),
                json.has("layer")
                        ? MoeItemPreferences.PreferenceLayer.fromString(GsonHelper.getAsString(json, "layer"))
                        : MoeItemPreferences.PreferenceLayer.fromSocialLayer(observer.get().inferredLayer()),
                GsonHelper.getAsFloat(json, "preference", 0.0F),
                GsonHelper.getAsFloat(json, "aversion", 0.0F),
                GsonHelper.getAsFloat(json, "interest", 0.0F),
                GsonHelper.getAsFloat(json, "begging", 0.0F)));
    }

    private static Optional<SocialAffinities.Matcher> parseObserver(JsonObject json) {
        Optional<SocialAffinities.BlockMatcher> block = SocialAffinities.parseBlockMatcher(
                optionalString(json, "block"),
                optionalString(json, "block_tag"));
        SocialAffinities.Matcher matcher = new SocialAffinities.Matcher(
                block,
                optionalString(json, "blood_type"),
                optionalString(json, "dere"),
                optionalString(json, "zodiac"),
                optionalString(json, "gender"),
                optionalString(json, "emotion"));
        return matcher.isEmpty() ? Optional.empty() : Optional.of(matcher);
    }

    private static Optional<MoeItemPreferences.ItemMatcher> parseItem(JsonObject json) {
        JsonObject itemJson = json.has("item") && json.get("item").isJsonObject()
                ? GsonHelper.getAsJsonObject(json, "item")
                : json;
        return MoeItemPreferences.parseItemMatcher(
                optionalString(itemJson, "item"),
                optionalString(itemJson, "item_tag"));
    }

    private static Optional<String> optionalString(JsonObject json, String key) {
        return json.has(key) ? Optional.of(GsonHelper.getAsString(json, key)) : Optional.empty();
    }
}
